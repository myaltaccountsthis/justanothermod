package me.myaltsthis.justanothermod.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import me.myaltsthis.justanothermod.JustAnotherModClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.awt.*;

// unused until i can figure out how to get it work
public class FoodHud extends MovableHud {
    private final MinecraftClient client;

    public FoodHud(MinecraftClient minecraftClient) {
        super(0, 0, 20, 9);
        this.client = minecraftClient;
    }

    public void render(MatrixStack matrices, PlayerEntity player) {
        if (player == null)
            return;

        int x = this.client.getWindow().getScaledWidth() / 2 + 91;
        int y = this.client.getWindow().getScaledHeight() - 38;

        NbtCompound nbt = new NbtCompound();
        player.getHungerManager().writeNbt(nbt);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        TextRenderer textRenderer = this.client.textRenderer;

        float totalFood = nbt.getInt("foodLevel") + nbt.getFloat("foodSaturationLevel") - nbt.getFloat("foodExhaustionLevel") / 4;
        JustAnotherModClient.LOGGER.info(totalFood + ": " + nbt.getInt("foodLevel") + " " + nbt.getFloat("foodExhaustionLevel"));
        String str = "%.2f".formatted(totalFood);
        textRenderer.draw(matrices, str, x, y, getFoodColor(totalFood / 40));
    }

    private int getFoodColor(float ratio) {
        ratio = Math.min(Math.max(ratio, 0), 1);
        float r, g, b;
        r = Math.min(Math.max(1 - ratio / 2, 0), 1);
        g = Math.min(Math.max(ratio / 2 + .25f, 0), 1);
        b = Math.min(Math.max(1 - Math.abs(ratio * 4 - 2) / 2, 0), .5f) / 4;
        return new Color(r, g, b).getRGB();
    }
}
