package me.myaltsthis.justanothermod.enums;

import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.Comparator;

public enum ScanMode {
    MOB_SPAWN(0, "justanothermod.options.scanMode.mobSpawn"),
    DIAMONDS(1, "justanothermod.options.scanMode.diamonds"),
    TEXT(2, "justanothermod.options.scanMode.text");

    private static final ScanMode[] VALUES;
    private final int id;
    private final String translationKey;

    ScanMode(int j, String string2) {
        this.id = j;
        this.translationKey = string2;
    }

    public int getId() {
        return this.id;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public static ScanMode byId(int id) {
        return VALUES[MathHelper.floorMod(id, VALUES.length)];
    }

    static {
        VALUES = Arrays.stream(ScanMode.values()).sorted(Comparator.comparingInt(ScanMode::getId)).toArray(ScanMode[]::new);
    }
}