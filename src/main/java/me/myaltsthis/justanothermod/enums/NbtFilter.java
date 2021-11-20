package me.myaltsthis.justanothermod.enums;

import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.Comparator;

public enum NbtFilter {
    NO_FILTER(0, "justanothermod.options.nbtFilter.noFilter"),
    DETAILED(1, "justanothermod.options.nbtFilter.detailed"),
    BASIC(2, "justanothermod.options.nbtFilter.basic");

    private static final NbtFilter[] VALUES;
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
        return VALUES[MathHelper.floorMod(id, VALUES.length)];
    }

    static {
        VALUES = Arrays.stream(NbtFilter.values()).sorted(Comparator.comparingInt(NbtFilter::getId)).toArray(NbtFilter[]::new);
    }
}