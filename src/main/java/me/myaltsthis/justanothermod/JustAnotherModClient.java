package me.myaltsthis.justanothermod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.myaltsthis.justanothermod.enums.NbtFilter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Environment(EnvType.CLIENT)
public class JustAnotherModClient implements ClientModInitializer {
    private static final HashMap<NbtFilter, List<String>> keyFilter = new HashMap<>() {{
        put(NbtFilter.NO_FILTER, new ArrayList<>());
        put(NbtFilter.DETAILED, List.of("CustomName", "OnGround", "UUID", "AbsorptionAmount", "ActiveEffects", "CanPickUpLoot", "Health", "HandDropChances", "HandItems", "PersistenceRequired", "playerGameType", "Score", "SpawnX", "SpawnY", "SpawnZ", "SpawnForced", "foodLevel", "foodSaturationLevel", "abilities"));
    }};

    public static Logger LOGGER = LogManager.getLogger("JustAnotherMod");
    @Override
    public void onInitializeClient() {
        LOGGER.info("Checking options: " + MyGameOptions.zoomAmount);
        KeyBinding copyNbt = MyGameOptions.keyCopyNbt;
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (copyNbt.wasPressed()) {
                if (MinecraftClient.getInstance().player != null) {
                    NbtCompound nbt = getEntityNbt();
                    MinecraftClient.getInstance().keyboard.setClipboard(JustAnotherModClient.toPrettyFormat(nbt.toString()));
                    MinecraftClient.getInstance().player.sendSystemMessage(new LiteralText("Copied entity NBT to clipboard").formatted(Formatting.GREEN), null);
                }
            }
        });
        LOGGER.info("loaded");
    }
    public static NbtCompound getEntityNbt(NbtFilter filterType) {
        Entity entity = MinecraftClient.getInstance().targetedEntity;
        if (entity != null) {
            NbtCompound nbt = entity.writeNbt(new NbtCompound());
            // TODO add CyclingOption later
            List<String> filter = keyFilter.get(filterType);
            if (!filter.isEmpty()) {
                for (String key : nbt.getKeys()) {
                    if (!filter.contains(key)) {
                        nbt.remove(key);
                    }
                }
            }
            return nbt;
        }
        return MinecraftClient.getInstance().player != null ? MinecraftClient.getInstance().player.writeNbt(new NbtCompound()) : new NbtCompound();
    }
    public static NbtCompound getEntityNbt() {
        return getEntityNbt(NbtFilter.NO_FILTER);
    }
    public static String toPrettyFormat(String jsonString)
    {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.toJson(json);
    }

}
