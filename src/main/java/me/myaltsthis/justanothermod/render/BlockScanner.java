package me.myaltsthis.justanothermod.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;

import java.util.ArrayList;

public class BlockScanner {
    public static final ArrayList<BlockPos> blocksToRender = new ArrayList<>();

    public static void run() {
        MinecraftClient instance = MinecraftClient.getInstance();
        final World world = instance.world;
        final PlayerEntity player = instance.player;

        if (world == null || player == null)
            return;
        EntityType<?> entityType = EntityType.ZOMBIE;
        ChunkManager chunkManager = world.getChunkManager();

        int range = Math.min(instance.options.viewDistance, 128 / 16);
        int cX = player.getChunkPos().x;
        int cZ = player.getChunkPos().z;
        Vec3d pPos = player.getPos();
        Vec3i playerPos = new Vec3i(pPos.x, pPos.y, pPos.z);

        blocksToRender.clear();

        for (int i = cX - range; i <= cX + range; i++) {
            for (int j = cZ - range; j <= cZ + range; j++) {
                Chunk chunk = chunkManager.getWorldChunk(i, j);
                if (chunk == null)
                    continue;
                ChunkPos chunkPos = chunk.getPos();
                for (BlockPos pos : BlockPos.iterate(chunkPos.getStartX(), 0, chunkPos.getStartZ(), chunkPos.getEndX(), 255, chunkPos.getEndZ())) {
                    if (pos.getSquaredDistance(playerPos) < 128 * 128 &&
                            world.isSpaceEmpty(entityType.createSimpleBoundingBox((double) pos.getX() + 0.5, pos.getY() + 1, (double) pos.getZ() + 0.5)) &&
                            !SpawnHelper.canSpawn(SpawnRestriction.getLocation(entityType), world, pos, entityType)
                    ) {
                        blocksToRender.add(pos);
                        System.out.println(pos);
                    }
                }
                System.out.println(i + "," + j);
            }
        }
    }
}
