package me.myaltsthis.justanothermod;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import me.myaltsthis.justanothermod.enums.NbtFilter;
import me.myaltsthis.justanothermod.render.MonkeyRenderType;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

public abstract class MyGameOptions {
    static final Logger LOGGER = LogManager.getLogger();
    static final Splitter COLON_SPLITTER = Splitter.on(":").limit(2);
    
    private static final File optionsFile;
    public static boolean enhancedMovement = false;
    public static boolean fog = false;
    public static double zoomAmount = 4.0D;
    public static double maxFOV = 110.0F;
    public static int scanDistance = 4;
    public static double scanAlphaOffset = -.3;
    public static double scanLineWidth = 3.0D;
    public static NbtFilter nbtFilter = NbtFilter.BASIC;

    public static final KeyBinding keyZoom;
    public static final KeyBinding keyShowTooltip;
    public static final KeyBinding keyCopyNbt;
    
    private static void accept(MyGameOptions.Visitor visitor) {
        enhancedMovement = visitor.visitBoolean("enhancedMovement", enhancedMovement);
        fog = visitor.visitBoolean("fog", fog);
        zoomAmount = visitor.visitDouble("zoomAmount", zoomAmount);
        maxFOV = visitor.visitDouble("maxFOV", maxFOV);
        scanDistance = visitor.visitInt("scanDistance", scanDistance);
        scanAlphaOffset = visitor.visitDouble("scanAlphaOffset", scanAlphaOffset);
        scanLineWidth = visitor.visitDouble("scanLineWidth", scanLineWidth);
        nbtFilter = visitor.visitObject("nbtFilter", nbtFilter, NbtFilter::byId, NbtFilter::getId);
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

            MyGameOptions.accept(new MyGameOptions.Visitor() {
                @Nullable
                private String find(String key) {
                    return nbtCompound2.contains(key) ? nbtCompound2.getString(key) : null;
                }

                public int visitInt(String key, int current) {
                    String string = this.find(key);
                    if (string != null) {
                        try {
                            return Integer.parseInt(string);
                        } catch (NumberFormatException var5) {
                            MyGameOptions.LOGGER.warn("Invalid integer value for option {} = {}", key, string, var5);
                        }
                    }

                    return current;
                }

                public boolean visitBoolean(String key, boolean current) {
                    String string = this.find(key);
                    return string != null ? MyGameOptions.isTrue(string) : current;
                }

                public String visitString(String key, String current) {
                    return MoreObjects.firstNonNull(this.find(key), current);
                }

                public double visitDouble(String key, double current) {
                    String string = this.find(key);
                    if (string != null) {
                        if (MyGameOptions.isTrue(string)) {
                            return 1.0D;
                        }

                        if (MyGameOptions.isFalse(string)) {
                            return 0.0D;
                        }

                        try {
                            return Double.parseDouble(string);
                        } catch (NumberFormatException var6) {
                            MyGameOptions.LOGGER.warn("Invalid floating point value for option {} = {}", key, string, var6);
                        }
                    }

                    return current;
                }

                public float visitFloat(String key, float current) {
                    String string = this.find(key);
                    if (string != null) {
                        if (MyGameOptions.isTrue(string)) {
                            return 1.0F;
                        }

                        if (MyGameOptions.isFalse(string)) {
                            return 0.0F;
                        }

                        try {
                            return Float.parseFloat(string);
                        } catch (NumberFormatException var5) {
                            LOGGER.warn("Invalid floating point value for option {} = {}", key, string, var5);
                        }
                    }

                    return current;
                }

                public <T> T visitObject(String key, T current, Function<String, T> decoder, Function<T, String> encoder) {
                    String string = this.find(key);
                    return string == null ? current : decoder.apply(string);
                }

                public <T> T visitObject(String key, T current, IntFunction<T> decoder, ToIntFunction<T> encoder) {
                    String string = this.find(key);
                    if (string != null) {
                        try {
                            return decoder.apply(Integer.parseInt(string));
                        } catch (Exception var7) {
                            LOGGER.warn("Invalid integer value for option {} = {}", key, string, var7);
                        }
                    }

                    return current;
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
                accept(new MyGameOptions.Visitor() {
                    public void print(String key) {
                        printWriter.print(key);
                        printWriter.print(':');
                    }

                    public int visitInt(String key, int current) {
                        this.print(key);
                        printWriter.println(current);
                        return current;
                    }

                    public boolean visitBoolean(String key, boolean current) {
                        this.print(key);
                        printWriter.println(current);
                        return current;
                    }

                    public String visitString(String key, String current) {
                        this.print(key);
                        printWriter.println(current);
                        return current;
                    }

                    public double visitDouble(String key, double current) {
                        this.print(key);
                        printWriter.println(current);
                        return current;
                    }

                    public float visitFloat(String key, float current) {
                        this.print(key);
                        printWriter.println(current);
                        return current;
                    }

                    public <T> T visitObject(String key, T current, Function<String, T> decoder, Function<T, String> encoder) {
                        this.print(key);
                        printWriter.println(encoder.apply(current));
                        return current;
                    }

                    public <T> T visitObject(String key, T current, IntFunction<T> decoder, ToIntFunction<T> encoder) {
                        this.print(key);
                        printWriter.println(encoder.applyAsInt(current));
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

    private interface Visitor {
        int visitInt(String key, int current);

        boolean visitBoolean(String key, boolean current);

        String visitString(String key, String current);

        double visitDouble(String key, double current);

        float visitFloat(String key, float current);

        <T> T visitObject(String key, T current, Function<String, T> decoder, Function<T, String> encoder);

        <T> T visitObject(String key, T current, IntFunction<T> decoder, ToIntFunction<T> encoder);
    }
    
    static {
        optionsFile = new File(MinecraftClient.getInstance().runDirectory, "optionsjam.txt");
        keyZoom = KeyBindingHelper.registerKeyBinding(new KeyBinding("justanothermod.key.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, "key.categories.misc"));
        keyShowTooltip = KeyBindingHelper.registerKeyBinding(new KeyBinding("justanothermod.key.showTooltip", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, "key.categories.misc"));
        keyCopyNbt = KeyBindingHelper.registerKeyBinding(new KeyBinding("justanothermod.key.copyNbt", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_CONTROL, "key.categories.misc"));

        MyGameOptions.load();
    }
}
