package me.myaltsthis.justanothermod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.myaltsthis.justanothermod.enums.NbtFilter;
import me.myaltsthis.justanothermod.hud.LoggerHud;
import me.myaltsthis.justanothermod.render.BlockScanner;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class JustAnotherModClient implements ClientModInitializer {
    private static final HashMap<NbtFilter, List<String>> keyFilter = new HashMap<>() {{
        put(NbtFilter.NO_FILTER, new ArrayList<>());
        put(NbtFilter.DETAILED, List.of("CustomName", "OnGround", "UUID", "AbsorptionAmount", "ActiveEffects", "CanPickUpLoot", "Health", "HandDropChances", "HandItems", "PersistenceRequired", "playerGameType", "Score", "foodLevel", "foodSaturationLevel", "abilities", "Inventory"));
        put(NbtFilter.BASIC, List.of("CustomName", "UUID", "AbsorptionAmount", "ActiveEffects", "Health", "playerGameType", "Score", "foodLevel", "foodSaturationLevel", "abilities"));
    }};

    public static Logger LOGGER = LogManager.getLogger("JustAnotherMod");
    public static LoggerHud loggerHud;
    public static boolean loggerActive = false;
    public static boolean jumpNextTick = false;
    public static boolean infinitePlace = false;
    public static ClickInterval interval = null; // make class for this that holds data: [left|right|stop (null)] [interval]
    public static int intervalDelay = 0;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Checking options: " + MyGameOptions.zoomAmount);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // handle inputs
            while (MyGameOptions.keyCopyNbt.wasPressed()) {
                if (MinecraftClient.getInstance().player != null) {
                    NbtCompound nbt = getEntityNbt();
                    MinecraftClient.getInstance().keyboard.setClipboard(JustAnotherModClient.toPrettyFormat(nbt.toString()));
                    MinecraftClient.getInstance().player.sendMessage(Text.literal("Copied entity NBT to clipboard").formatted(Formatting.GREEN), false);
                }
            }
            while (MyGameOptions.keyRefreshScan.wasPressed()) Util.getMainWorkerExecutor().execute(new BlockScanner(true));
            while (MyGameOptions.keyClearScan.wasPressed()) BlockScanner.blocksToRender.clear();
            while (MyGameOptions.keySamePosScan.wasPressed()) Util.getMainWorkerExecutor().execute(new BlockScanner(false));
            while (MyGameOptions.keyTogglePacketLog.wasPressed()) loggerActive = !loggerActive;
            while (MyGameOptions.keyInfinitePlace.wasPressed()) {
                infinitePlace = !infinitePlace;
                PlayerEntity player = MinecraftClient.getInstance().player;
                if (player != null) {
                    player.sendMessage(Text.translatable("justanothermod.key.infinitePlace").append(" " + (infinitePlace ? "enabled" : "disabled")).formatted(Formatting.GREEN), false);
                }
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            JustAnotherModClient.interval = null;
        });
        ClientTickEvents.START_WORLD_TICK.register(world -> {
            if (interval != null) {
                intervalDelay--;
            }
        });
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (interval != null) {
                if (intervalDelay <= 0) {
                    intervalDelay = interval.interval;
                    if (interval.isRightHand) {
                        MinecraftClient.getInstance().doItemUse();
                    } else {
                        MinecraftClient.getInstance().doAttack();
                    }
                }
            }
        });
        ClientCommandManager.getActiveDispatcher().register(ClientCommandManager.literal("interval").executes(context -> {
            context.getSource().sendFeedback(Text.literal("Use command with arguments"));
            return 0;
        }).then(ClientCommandManager.argument("type", StringArgumentType.word()).suggests(new IntervalTypeSuggestionProvider()).executes(context -> {
            switch (StringArgumentType.getString(context, "type").toLowerCase()) {
                case "left", "right" -> context.getSource().sendFeedback(Text.literal("Must provide interval"));
                case "show" -> context.getSource().sendFeedback(Text.literal("Currently running " + (JustAnotherModClient.interval != null ? JustAnotherModClient.interval : "nothing")).formatted(Formatting.GREEN));
                case "stop" -> {
                    JustAnotherModClient.interval = null;
                    context.getSource().sendFeedback(Text.literal("Stopped interval").formatted(Formatting.GREEN));
                }
                default -> context.getSource().sendFeedback(Text.literal("Invalid argument").formatted(Formatting.RED));
            }
            return 1;
        }).then(ClientCommandManager.argument("interval", IntegerArgumentType.integer()).executes(context -> {
            int interval = IntegerArgumentType.getInteger(context, "interval");
            switch (StringArgumentType.getString(context, "type").toLowerCase()) {
                case "left" -> {
                    intervalDelay = interval;
                    JustAnotherModClient.interval = new ClickInterval(false, interval);
                    context.getSource().sendFeedback(Text.literal("Started interval: " + JustAnotherModClient.interval).formatted(Formatting.GREEN));
                }
                case "right" -> {
                    intervalDelay = interval;
                    JustAnotherModClient.interval = new ClickInterval(true, interval);
                    context.getSource().sendFeedback(Text.literal("Started interval: " + JustAnotherModClient.interval).formatted(Formatting.GREEN));
                }
                case "show" -> context.getSource().sendFeedback(Text.literal("Currently running " + (JustAnotherModClient.interval != null ? JustAnotherModClient.interval : "nothing")).formatted(Formatting.GREEN));
                case "stop" -> {
                    JustAnotherModClient.interval = null;
                    context.getSource().sendFeedback(Text.literal("Stopped interval").formatted(Formatting.GREEN));
                }
                default -> context.getSource().sendFeedback(Text.literal("Invalid argument").formatted(Formatting.RED));
            }
            return 1;
        }))));
        LOGGER.info("loaded");
    }
    public static NbtCompound getEntityNbt(NbtFilter filterType) {
        Entity entity = MinecraftClient.getInstance().targetedEntity;
        if (entity != null) {
            NbtCompound nbt = entity.writeNbt(new NbtCompound());
            List<String> filter = keyFilter.get(filterType);
            if (!filter.isEmpty()) {
                ArrayList<String> keysToRemove = new ArrayList<>();
                for (String key : nbt.getKeys()) {
                    if (!filter.contains(key)) {
                        keysToRemove.add(key);
                    }
                }
                for (String key : keysToRemove)
                    nbt.remove(key);
            }
            return nbt;
        }
        return MinecraftClient.getInstance().player != null ? MinecraftClient.getInstance().player.writeNbt(new NbtCompound()) : new NbtCompound();
    }
    public static NbtCompound getEntityNbt() {
        return getEntityNbt(MyGameOptions.nbtFilter.getValue());
    }
    public static String toPrettyFormat(String jsonString)
    {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.toJson(json);
    }

    protected static class ClickInterval {
        public final boolean isRightHand;
        public final int interval;

        public ClickInterval(boolean isRightHand) {
            this(isRightHand, 20);
        }
        public ClickInterval(boolean isRightHand, int interval) {
            this.isRightHand = isRightHand;
            this.interval = interval;
        }

        @Override
        public String toString() {
            return (isRightHand ? "Right" : "Left") + "@" + interval + "gt";
        }
    }
    private static class IntervalTypeSuggestionProvider implements SuggestionProvider<FabricClientCommandSource> {
        private static final String[] arguments = {"left", "right", "show", "stop"};

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
            for (String argument : arguments)
                builder.suggest(argument);
            return builder.buildFuture();
        }
    }
}
