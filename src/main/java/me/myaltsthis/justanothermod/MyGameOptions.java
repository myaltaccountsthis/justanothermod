package me.myaltsthis.justanothermod;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import me.myaltsthis.justanothermod.enums.NbtFilter;
import me.myaltsthis.justanothermod.enums.ScanMode;
import me.myaltsthis.justanothermod.mixin.GameOptionsMixin;
import me.myaltsthis.justanothermod.render.MonkeyRenderType;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.InputUtil;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

public abstract class MyGameOptions {
    static final Logger LOGGER = LogManager.getLogger();
    static final Splitter COLON_SPLITTER = Splitter.on(":").limit(2);

    private static final File optionsFile;
    public static String blockToScan = "";

    public static final SimpleOption<Boolean> enhancedMovement = SimpleOption.ofBoolean("justanothermod.options.enhancedMovement", false);
    public static final SimpleOption<Boolean> allowElytraBounce = SimpleOption.ofBoolean("justanothermod.options.elytraBounce", false);
    public static final SimpleOption<Boolean> fog = SimpleOption.ofBoolean("justanothermod.options.fog", false);
    public static final SimpleOption<Boolean> autoFish = SimpleOption.ofBoolean("justanothermod.options.autoFish", false);
    public static final SimpleOption<Boolean> transparentBackground = SimpleOption.ofBoolean("justanothermod.options.transparentBackground", false);
    public static final SimpleOption<Boolean> showHud = SimpleOption.ofBoolean("justanothermod.options.showHud", false);
    public static final SimpleOption<Boolean> overrideBrightness = SimpleOption.ofBoolean("justanothermod.options.overrideBrightness", false);
    public static final SimpleOption<Double> brightness = new SimpleOption<Double>("justanothermod.options.customGamma", SimpleOption.emptyTooltip(), GameOptions::getPercentValueText, new SimpleOption.ValidatingIntSliderCallbacks(0, 100).withModifier(sliderProgressValue -> sliderProgressValue / 10.0, value -> (int) (value * 10)), Codec.doubleRange(0.0D, 10.0D), 1.0, value -> {});
    public static final SimpleOption<Double> zoomAmount = new SimpleOption<>("justanothermod.options.zoomAmount",  SimpleOption.emptyTooltip(), GameOptions::getPercentValueText, new SimpleOption.ValidatingIntSliderCallbacks(10, 100).withModifier(sliderProgressValue -> sliderProgressValue / 10.0, value -> (int) (value * 10)), Codec.doubleRange(1.0, 10.0), 4.0, value -> {});
    //public static final SimpleOption<Double> MAX_FOV = new SimpleOption<Double>("justanothermod.options.maxFOV", 30.0D, 360.0D, 1.0F, gameOptions -> MyGameOptions.maxFOV, (gameOptions, maxZoom) -> MyGameOptions.maxFOV = maxZoom, (gameOptions, option) -> Text.translatable("justanothermod.options.maxFOV")).append(": " + Math.round(option.get(gameOptions))));
    public static final SimpleOption<Integer> scanDistance = new SimpleOption<>("justanothermod.options.scanDistance", SimpleOption.emptyTooltip(), GameOptions::getGenericValueText, new SimpleOption.ValidatingIntSliderCallbacks(0, 8), Codec.intRange(0, 8), 4, value -> {});
    public static final SimpleOption<Double> scanAlphaOffset = new SimpleOption<>("justanothermod.options.scanAlphaOffset", SimpleOption.emptyTooltip(), MyGameOptions::getGenericValueText, new SimpleOption.ValidatingIntSliderCallbacks(-10, 10).withModifier(sliderProgressValue -> sliderProgressValue / 10.0, value -> (int) (value * 10)), Codec.doubleRange(-1.0D, 1.0D), -.3, value -> {});
    public static final SimpleOption<Double> scanLineWidth = new SimpleOption<>("justanothermod.options.scanLineWidth", SimpleOption.emptyTooltip(), (prefix, value) -> MutableText.of(getGenericValueText(prefix, value).getContent()).append("px"), new SimpleOption.ValidatingIntSliderCallbacks(0, 10).withModifier(sliderProgressValue -> sliderProgressValue / 2.0, value -> (int) (value * 2)), Codec.doubleRange(0.0D, 5.0D), 3.0, scanLineWidth -> MonkeyRenderType.refreshOverlayLines());
    public static final SimpleOption<ScanMode> scanMode = new SimpleOption<>("justanothermod.options.scanMode", SimpleOption.emptyTooltip(), SimpleOption.enumValueText(), new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(ScanMode.values()), Codec.INT.xmap(ScanMode::byId, ScanMode::getId)), ScanMode.MOB_SPAWN, value -> {});
    public static final SimpleOption<NbtFilter> nbtFilter = new SimpleOption<NbtFilter>("justanothermod.options.nbtFilter", SimpleOption.emptyTooltip(), SimpleOption.enumValueText(),new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(NbtFilter.values()), Codec.INT.xmap(NbtFilter::byId, NbtFilter::getId)), NbtFilter.BASIC, value -> {});


    public static Text getGenericValueText(Text prefix, Object value) {
        return Text.translatable("justanothermod.options.generic_value", prefix, Text.literal(value.toString()));
    }

    /*
    public static boolean enhancedMovement = false;
    public static boolean allowElytraBounce = false;
    public static boolean fog = false;
    public static boolean autoFish = false;
    public static boolean transparentBackground = false;
    public static boolean showHud = false;
    public static double zoomAmount = 4.0D;
    public static double maxFOV = 110.0F;
    public static int scanDistance = 4;
    public static double scanAlphaOffset = -.3;
    public static double scanLineWidth = 3.0D;
    public static NbtFilter nbtFilter = NbtFilter.BASIC;
    public static ScanMode scanMode = ScanMode.MOB_SPAWN;
     */

    public static final KeyBinding keyZoom;
    public static final KeyBinding keyShowTooltip;
    public static final KeyBinding keyCopyNbt;

    public static final KeyBinding keyRefreshScan;
    public static final KeyBinding keyClearScan;
    public static final KeyBinding keySamePosScan;

    public static final KeyBinding keyInfinitePlace;
    public static final KeyBinding keyTogglePacketLog;

    private static void accept(GameOptions.Visitor visitor) {
        visitor.accept("enhancedMovement", enhancedMovement);
        visitor.accept("elytraBounce", allowElytraBounce);
        visitor.accept("fog", fog);
        visitor.accept("autoFish", autoFish);
        visitor.accept("transparentBackground", transparentBackground);
        visitor.accept("showHud", showHud);
        visitor.accept("overrideBrightness", overrideBrightness);
        visitor.accept("zoomAmount", zoomAmount);
        //visitor.accept("maxFOV", maxFOV);
        visitor.accept("scanDistance", scanDistance);
        visitor.accept("scanAlphaOffset", scanAlphaOffset);
        visitor.accept("scanLineWidth", scanLineWidth);
        blockToScan = visitor.visitString("blockToScan", blockToScan);
        visitor.accept("nbtFilter", nbtFilter);
        visitor.accept("scanMode", scanMode);
    }

    public static void load() {
        try {
            if (!MyGameOptions.optionsFile.exists()) {
                return;
            }
            NbtCompound nbtCompound = new NbtCompound();
            BufferedReader bufferedReader = Files.newReader(MyGameOptions.optionsFile, Charsets.UTF_8);

            try {
                bufferedReader.lines().forEach((line) -> {
                    try {
                        Iterator<String> iterator = COLON_SPLITTER.split(line).iterator();
                        nbtCompound.putString(iterator.next(), iterator.next());
                    } catch (Exception var3) {
                        MyGameOptions.LOGGER.warn("Skipping bad option: {}", line);
                    }

                });
            } catch (Throwable var6) {
                try {
                    bufferedReader.close();
                } catch (Throwable var5) {
                    var6.addSuppressed(var5);
                }

                throw var6;
            }

            bufferedReader.close();

            final NbtCompound nbtCompound2 = MyGameOptions.update(nbtCompound);

            MyGameOptions.accept(new GameOptions.Visitor() {
                @Nullable
                private String find(String key) {
                    return nbtCompound2.contains(key) ? nbtCompound2.getString(key) : null;
                }

                @Override
                public <T> void accept(String key, SimpleOption<T> option) {
                    String string = this.find(key);
                    if (string != null) {
                        JsonReader jsonReader = new JsonReader(new StringReader(string.isEmpty() ? "\"\"" : string));
                        JsonElement jsonElement = JsonParser.parseReader(jsonReader);
                        DataResult<T> dataResult = option.getCodec().parse(JsonOps.INSTANCE, jsonElement);
                        dataResult.error().ifPresent(partialResult -> LOGGER.error("Error parsing option value " + string + " for option " + option + ": " + partialResult.message()));
                        dataResult.result().ifPresent(option::setValue);
                    }
                }

                @Override
                public int visitInt(String key, int current) {
                    String string = this.find(key);
                    if (string != null) {
                        try {
                            return Integer.parseInt(string);
                        }
                        catch (NumberFormatException numberFormatException) {
                            LOGGER.warn("Invalid integer value for option {} = {}", key, string, numberFormatException);
                        }
                    }
                    return current;
                }

                @Override
                public boolean visitBoolean(String key, boolean current) {
                    String string = this.find(key);
                    return string != null ? GameOptionsMixin.isTrue(string) : current;
                }

                @Override
                public String visitString(String key, String current) {
                    return MoreObjects.firstNonNull(this.find(key), current);
                }

                @Override
                public float visitFloat(String key, float current) {
                    String string = this.find(key);
                    if (string != null) {
                        if (GameOptionsMixin.isTrue(string)) {
                            return 1.0f;
                        }
                        if (GameOptionsMixin.isFalse(string)) {
                            return 0.0f;
                        }
                        try {
                            return Float.parseFloat(string);
                        }
                        catch (NumberFormatException numberFormatException) {
                            LOGGER.warn("Invalid floating point value for option {} = {}", key, string, numberFormatException);
                        }
                    }
                    return current;
                }

                @Override
                public <T> T visitObject(String key, T current, Function<String, T> decoder, Function<T, String> encoder) {
                    String string = this.find(key);
                    return string == null ? current : decoder.apply(string);
                }
            });

            KeyBinding.updateKeysByCode();
        } catch (Exception var7) {
            LOGGER.error("Failed to load options", var7);
        }

    }
    static boolean isTrue(String value) {
        return "true".equals(value);
    }
    static boolean isFalse(String value) {
        return "false".equals(value);
    }
    
    private static NbtCompound update(NbtCompound nbt) {
        int i = 0;

        try {
            i = Integer.parseInt(nbt.getString("version"));
        } catch (RuntimeException var4) {
            var4.printStackTrace();
        }

        return NbtHelper.update(MinecraftClient.getInstance().getDataFixer(), DataFixTypes.OPTIONS, nbt, i);
    }
    
    public static void write() {
        try {
            final PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(MyGameOptions.optionsFile), StandardCharsets.UTF_8));

            try {
                printWriter.println("version:" + SharedConstants.getGameVersion().getWorldVersion());
                accept(new GameOptions.Visitor() {
                    public void print(String key) {
                        printWriter.print(key);
                        printWriter.print(':');
                    }

                    @Override
                    public <T> void accept(String key, SimpleOption<T> option) {
                        DataResult<JsonElement> dataResult = option.getCodec().encodeStart(JsonOps.INSTANCE, (T) option.getValue());
                        dataResult.error().ifPresent(partialResult -> LOGGER.error("Error saving option " + option + ": " + partialResult));
                        dataResult.result().ifPresent(json -> {
                            this.print(key);
                            printWriter.println(GameOptionsMixin.getGSON().toJson((JsonElement)json));
                        });
                    }

                    @Override
                    public int visitInt(String key, int current) {
                        this.print(key);
                        printWriter.println(current);
                        return current;
                    }

                    @Override
                    public boolean visitBoolean(String key, boolean current) {
                        this.print(key);
                        printWriter.println(current);
                        return current;
                    }

                    @Override
                    public String visitString(String key, String current) {
                        this.print(key);
                        printWriter.println(current);
                        return current;
                    }

                    @Override
                    public float visitFloat(String key, float current) {
                        this.print(key);
                        printWriter.println(current);
                        return current;
                    }

                    @Override
                    public <T> T visitObject(String key, T current, Function<String, T> decoder, Function<T, String> encoder) {
                        this.print(key);
                        printWriter.println(encoder.apply(current));
                        return current;
                    }
                });
            } catch (Throwable throwable) {
                try {
                    printWriter.close();
                } catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }

                throw throwable;
            }

            printWriter.close();
        } catch (Exception var6) {
            MyGameOptions.LOGGER.error("Failed to save options", var6);
        }
        //LOGGER.log(Level.INFO, "Wrote to file");
    }
    
    static {
        optionsFile = new File(MinecraftClient.getInstance().runDirectory, "optionsjam.txt");
        keyZoom = KeyBindingHelper.registerKeyBinding(new KeyBinding("justanothermod.key.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, "key.categories.misc"));
        keyShowTooltip = KeyBindingHelper.registerKeyBinding(new KeyBinding("justanothermod.key.showTooltip", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, "key.categories.misc"));
        keyCopyNbt = KeyBindingHelper.registerKeyBinding(new KeyBinding("justanothermod.key.copyNbt", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_CONTROL, "key.categories.misc"));

        keyRefreshScan = KeyBindingHelper.registerKeyBinding(new KeyBinding("justanothermod.key.scan.refresh", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_BACKSLASH, "key.categories.misc"));
        keyClearScan = KeyBindingHelper.registerKeyBinding(new KeyBinding("justanothermod.key.scan.clear", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_BRACKET, "key.categories.misc"));
        keySamePosScan = KeyBindingHelper.registerKeyBinding(new KeyBinding("justanothermod.key.scan.samePos", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.categories.misc"));

        keyTogglePacketLog = KeyBindingHelper.registerKeyBinding(new KeyBinding("justanothermod.key.packet.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "key.categories.misc"));

        keyInfinitePlace = KeyBindingHelper.registerKeyBinding(new KeyBinding("justanothermod.key.infinitePlace", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "key.categories.misc"));

        MyGameOptions.load();
    }
}
