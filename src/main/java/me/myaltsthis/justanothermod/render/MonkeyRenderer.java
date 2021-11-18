package me.myaltsthis.justanothermod.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class MonkeyRenderer {
    public static void render(MatrixStack matrices, Camera camera) {
        final Vec3d cameraPos = camera.getPos();
        VertexConsumerProvider.Immediate entityVertexConsumers =
                MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer builder = entityVertexConsumers.getBuffer(MonkeyRenderType.OVERLAY_LINES);

        PlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;

        matrices.push();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        RenderSystem.disableDepthTest();

        for (BlockPos pos : BlockScanner.blocksToRender) {
            WorldRenderer.drawBox(matrices, builder, new Box(pos), 1f, 0f, 1f, 1);
        }

        matrices.pop();
        entityVertexConsumers.draw(MonkeyRenderType.OVERLAY_LINES);
        RenderSystem.enableDepthTest();

    }
}
