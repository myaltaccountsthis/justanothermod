package me.myaltsthis.justanothermod.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

import java.awt.*;

public class HealthHud extends MovableHud {
    private final MinecraftClient client;

    public HealthHud(MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }

    public void render(MatrixStack matrices, PlayerEntity player) {
        if (player == null)
            return;

        int x = this.client.getWindow().getScaledWidth() / 2 - 91;
        int y = this.client.getWindow().getScaledHeight() - 38; // in line with hearts

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        TextRenderer textRenderer = this.client.textRenderer;

        //drawHeart(matrices, x, y, 16);
        //drawHeart(matrices, x, y, 52);
        String str = "%.2f".formatted(player.getHealth());
        textRenderer.draw(matrices, str, x - textRenderer.getWidth(str), y, getHealthColor(player.getHealth() / player.getMaxHealth()));
        // draw background heart and full heart
    }

    private int getHealthColor(float ratio) {
        // 1, 0, 0 -> 1, .5, .5 -> 1, 1, .5 -> .5, 1, .5 -> 0, 1, 0
        ratio = Math.min(Math.max(ratio, 0), 1);
        float r, g, b;
        r = Math.min(Math.max(2 - ratio * 2, 0), 1);
        g = Math.min(Math.max(ratio * 2, 0), 1);
        b = Math.min(Math.max(1 - Math.abs(ratio * 4 - 2) / 2, 0), .5f);
        return new Color(r, g, b).getRGB();
    }

    private void drawHeart(MatrixStack matrices, int x, int y, int v) {
        drawTexture(matrices, x, y, v, 0, 9, 9);
    }
}
