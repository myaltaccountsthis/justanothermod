package me.myaltsthis.justanothermod.render;

import me.myaltsthis.justanothermod.JustAnotherModClient;
import me.myaltsthis.justanothermod.MyGameOptions;
import me.myaltsthis.justanothermod.enums.ScanMode;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import org.lwjgl.system.CallbackI;

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
        int cX = (int) Math.floor(blockPos.getX() / 16.0);
        int cZ = (int) Math.floor(blockPos.getZ() / 16.0);

        blocksToRender.clear();
        for (int i = cX - range; i <= cX + range; i++) {
            for (int j = cZ - range; j <= cZ + range; j++) {
                Chunk chunk = chunkManager.getWorldChunk(i, j);
                if (chunk == null)
                    continue;
                ChunkPos chunkPos = chunk.getPos();
                for (BlockPos pos : BlockPos.iterate(chunkPos.getStartX(), -64, chunkPos.getStartZ(), chunkPos.getEndX(), 319, chunkPos.getEndZ())) {
                    if (blocksToRender.size() == limit)
                        break;
                    checkBlock(pos, true);
                }
            }
        }
        JustAnotherModClient.LOGGER.info("Scanned and found " + blocksToRender.size() + " blocks");
    }

    private static boolean canMobSpawn(BlockPos pos, World world) {
        return pos.getSquaredDistance(scanOrigin) < 128 * 128 &&
                world.isSpaceEmpty(entityType.createSimpleBoundingBox((double) pos.getX() + 0.5, pos.getY(), (double) pos.getZ() + 0.5)) &&
                SpawnHelper.canSpawn(SpawnRestriction.getLocation(entityType), world, pos, entityType);
    }
    private static boolean isDiamond(BlockPos pos, World world) {
        String s = world.getBlockState(pos).getBlock().getTranslationKey();
        return s.equals(Blocks.DIAMOND_ORE.getTranslationKey()) || s.equals(Blocks.DEEPSLATE_DIAMOND_ORE.getTranslationKey());
    }

    public static void checkBlock(BlockPos pos, boolean isScanning) {
        MinecraftClient instance = MinecraftClient.getInstance();
        final World world = instance.world;
        final PlayerEntity player = instance.player;
        if (world == null || player == null)
            return;
        pos = pos.toImmutable();

        boolean success = false;
        ScanMode scanMode = MyGameOptions.scanMode;
        if (scanMode == ScanMode.MOB_SPAWN)
            success = canMobSpawn(pos, world);
        else if (scanMode == ScanMode.DIAMONDS) {
            success = isDiamond(pos, world);
            if (success)
                System.out.println(pos);
        }
        if (success) {
            if (isScanning || !blocksToRender.contains(pos))
                blocksToRender.add(pos);
        } else if (!isScanning) {
            int i = blocksToRender.indexOf(pos);
            if (i >= 0)
                blocksToRender.remove(i);
        }
    }
}
