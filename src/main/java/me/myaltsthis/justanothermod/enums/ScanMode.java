package me.myaltsthis.justanothermod.enums;

import net.minecraft.util.TranslatableOption;
import net.minecraft.util.math.MathHelper;

public enum ScanMode implements TranslatableOption {
    MOB_SPAWN(0, "justanothermod.options.scanMode.mobSpawn"),
    DIAMONDS(1, "justanothermod.options.scanMode.diamonds"),
    TEXT(2, "justanothermod.options.scanMode.text");

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
        return values()[MathHelper.floorMod(id, values().length)];
    }
}