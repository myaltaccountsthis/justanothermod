package me.myaltsthis.justanothermod.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.myaltsthis.justanothermod.MyGameOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;

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

            for (BlockPos pos : new ArrayList<>(BlockScanner.blocksToRender)) {
                double distance = Math.sqrt(pos.getSquaredDistance(player.getBlockPos()));
                double ratio = Math.min(distance / 128.0, 1.0);
                float alpha = Math.min(Math.max((float) (
                    (1 - ratio) * .9 + MyGameOptions.scanAlphaOffset.getValue()
                ), .1f), 1f);
                WorldRenderer.drawBox(matrices, builder, new Box(pos), 0f, 0f, 1f, alpha);
            }

            WorldRenderer.drawBox(matrices, builder, new Box(new BlockPos(BlockScanner.scanOrigin)), 0f, 1f, 0f, 1);

            matrices.pop();
            entityVertexConsumers.draw(MonkeyRenderType.OVERLAY_LINES);
            RenderSystem.enableDepthTest();
        }
    }
    public static void drawBox(BufferBuilder buffer, Box box, float r, float g, float b, float a) {
        drawBox(buffer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, r, g, b, a);
    }
    public static void drawBox(BufferBuilder buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b, float a) {
        buffer.vertex(minX, minY, minZ).color(r, g, b, a).next();
        buffer.vertex(minX, minY, maxZ).color(r, g, b, a).next();

        buffer.vertex(minX, minY, maxZ).color(r, g, b, a).next();
        buffer.vertex(minX, maxY, maxZ).color(r, g, b, a).next();

        buffer.vertex(minX, maxY, maxZ).color(r, g, b, a).next();
        buffer.vertex(minX, maxY, minZ).color(r, g, b, a).next();

        buffer.vertex(minX, maxY, minZ).color(r, g, b, a).next();
        buffer.vertex(minX, minY, minZ).color(r, g, b, a).next();

        // East side
        buffer.vertex(maxX, minY, maxZ).color(r, g, b, a).next();
        buffer.vertex(maxX, minY, minZ).color(r, g, b, a).next();

        buffer.vertex(maxX, minY, minZ).color(r, g, b, a).next();
        buffer.vertex(maxX, maxY, minZ).color(r, g, b, a).next();

        buffer.vertex(maxX, maxY, minZ).color(r, g, b, a).next();
        buffer.vertex(maxX, maxY, maxZ).color(r, g, b, a).next();

        buffer.vertex(maxX, maxY, maxZ).color(r, g, b, a).next();
        buffer.vertex(maxX, minY, maxZ).color(r, g, b, a).next();

        // North side (don't repeat the vertical lines that are done by the east/west sides)
        buffer.vertex(maxX, minY, minZ).color(r, g, b, a).next();
        buffer.vertex(minX, minY, minZ).color(r, g, b, a).next();

        buffer.vertex(minX, maxY, minZ).color(r, g, b, a).next();
        buffer.vertex(maxX, maxY, minZ).color(r, g, b, a).next();

        // South side (don't repeat the vertical lines that are done by the east/west sides)
        buffer.vertex(minX, minY, maxZ).color(r, g, b, a).next();
        buffer.vertex(maxX, minY, maxZ).color(r, g, b, a).next();

        buffer.vertex(maxX, maxY, maxZ).color(r, g, b, a).next();
        buffer.vertex(minX, maxY, maxZ).color(r, g, b, a).next();
    }
}
