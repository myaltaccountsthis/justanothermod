package me.myaltsthis.justanothermod.enums;

import net.minecraft.util.TranslatableOption;
import net.minecraft.util.math.MathHelper;

public enum NbtFilter implements TranslatableOption {
    NO_FILTER(0, "justanothermod.options.nbtFilter.noFilter"),
    DETAILED(1, "justanothermod.options.nbtFilter.detailed"),
    BASIC(2, "justanothermod.options.nbtFilter.basic");

    private final int id;
    private final String translationKey;

    NbtFilter(int j, String string2) {
        this.id = j;
        this.translationKey = string2;
    }

    public int getId() {
        return this.id;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public static NbtFilter byId(int id) {
        return values()[MathHelper.floorMod(id, values().length)];
    }
}