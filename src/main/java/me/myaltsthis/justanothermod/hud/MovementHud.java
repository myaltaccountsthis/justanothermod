package me.myaltsthis.justanothermod.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class MovementHud extends MovableHud {
    private final MinecraftClient client;

    public MovementHud(MinecraftClient minecraftClient) {
        super(0, 0, 50, 9);
        this.client = minecraftClient;
    }

    public void render(MatrixStack matrices, Entity camera) {
        if (camera == null)
            return;

        double dx = camera.getX() - camera.lastRenderX;
        double dz = camera.getZ() - camera.lastRenderZ;
        double dist = Math.sqrt(dx * dx + dz * dz);
        String str = "%.3fm/s".formatted(dist * 20);

        TextRenderer textRenderer = this.client.textRenderer;
        int x = this.client.getWindow().getScaledWidth() / 2 - 91;
        int textHeight = textRenderer.getWrappedLinesHeight(str, 100);
        int y = this.client.getWindow().getScaledHeight() - 38 + textHeight;

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        textRenderer.draw(matrices, str, x - textRenderer.getWidth(str), y, Color.WHITE.getRGB());
        // draw background heart and full heart
    }
}
