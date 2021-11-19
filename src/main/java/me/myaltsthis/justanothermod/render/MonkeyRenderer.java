package me.myaltsthis.justanothermod.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.myaltsthis.justanothermod.MyGameOptions;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class MonkeyRenderer {
    public static void render(MatrixStack matrices, Camera camera) {
        if (!BlockScanner.blocksToRender.isEmpty()) {
            final Vec3d cameraPos = camera.getPos();
            VertexConsumerProvider.Immediate entityVertexConsumers =
                    MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            VertexConsumer builder = entityVertexConsumers.getBuffer(MonkeyRenderType.OVERLAY_LINES);

            World world = MinecraftClient.getInstance().world;
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null || world == null)
                return;

            matrices.push();
            matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            RenderSystem.disableDepthTest();

            for (BlockPos pos : BlockScanner.blocksToRender) {
                double distance = Math.sqrt(pos.getSquaredDistance(player.getBlockPos()));
                double ratio = distance / 128;
                if (ratio > 1)
                    continue;
                float alpha = Math.max((float) (
                    (1 - ratio) * .9 - .3
                ), .1f);
                WorldRenderer.drawBox(matrices, builder, new Box(pos), 0f, 0f, 1f, alpha);
            }

            matrices.pop();
            entityVertexConsumers.draw(MonkeyRenderType.OVERLAY_LINES);
            RenderSystem.enableDepthTest();
        }
    }
}
