package me.myaltsthis.justanothermod.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class PlayerInfoHud extends DrawableHelper {
    private static PlayerInfoHud instance;
    private final MinecraftClient client;
    private final HealthHud healthHud;
    private final MovementHud movementHud;

    public PlayerInfoHud(MinecraftClient minecraftClient) {
        instance = this;
        this.client = minecraftClient;
        healthHud = new HealthHud(this.client);
        movementHud = new MovementHud(this.client);
    }
    public static PlayerInfoHud getInstance() {
        return instance != null ? instance : new PlayerInfoHud(MinecraftClient.getInstance());
    }

    public void render(MatrixStack matrices, float tickDelta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
        healthHud.render(matrices, client.player);
        movementHud.render(matrices, client.player);
    }
}
