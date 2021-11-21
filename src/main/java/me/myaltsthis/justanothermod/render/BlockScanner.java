package me.myaltsthis.justanothermod.render;

import me.myaltsthis.justanothermod.JustAnotherModClient;
import me.myaltsthis.justanothermod.MyGameOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;

import java.util.ArrayList;

public class BlockScanner implements Runnable {
    public static final ArrayList<BlockPos> blocksToRender = new ArrayList<>();
    private static final int limit = 65536;
    private static final EntityType<?> entityType = EntityType.ZOMBIE;
    public static BlockPos scanOrigin = null;
    private final boolean reposition;

    public BlockScanner(boolean reposition) {
        this.reposition = reposition;
    }

    @Override
    public void run() {
        MinecraftClient instance = MinecraftClient.getInstance();
        final World world = instance.world;
        final PlayerEntity player = instance.player;
        if (world == null || player == null)
            return;

        ChunkManager chunkManager = world.getChunkManager();

        int range = Math.min(instance.options.viewDistance, MyGameOptions.scanDistance);

        if (reposition || scanOrigin == null)
            scanOrigin = player.getBlockPos();
        BlockPos blockPos = scanOrigin;
        int cX = blockPos.getX() / 16;
        int cZ = blockPos.getZ() / 16;

        blocksToRender.clear();
        for (int i = cX - range; i <= cX + range; i++) {
            for (int j = cZ - range; j <= cZ + range; j++) {
                Chunk chunk = chunkManager.getWorldChunk(i, j);
                if (chunk == null)
                    continue;
                ChunkPos chunkPos = chunk.getPos();
                for (BlockPos pos : BlockPos.iterate(chunkPos.getStartX(), 0, chunkPos.getStartZ(), chunkPos.getEndX(), 255, chunkPos.getEndZ())) {
                    if (blocksToRender.size() == limit)
                        break;
                    checkBlock(pos, true);
                }
            }
        }
        JustAnotherModClient.LOGGER.info("Scanned and found " + blocksToRender.size() + " blocks");
    }
    public static void checkBlock(BlockPos pos, boolean isScanning) {
        MinecraftClient instance = MinecraftClient.getInstance();
        final World world = instance.world;
        final PlayerEntity player = instance.player;
        if (world == null || player == null)
            return;
        pos = pos.toImmutable();
        if (pos.getSquaredDistance(scanOrigin) < 128 * 128 &&
                world.isSpaceEmpty(entityType.createSimpleBoundingBox((double) pos.getX() + 0.5, pos.getY(), (double) pos.getZ() + 0.5)) &&
                SpawnHelper.canSpawn(SpawnRestriction.getLocation(entityType), world, pos, entityType)) {
            if (isScanning || !blocksToRender.contains(pos))
                blocksToRender.add(pos);
        } else if (!isScanning) {
            System.out.println("removing " + pos);
            blocksToRender.remove(pos);
        }
    }
}
