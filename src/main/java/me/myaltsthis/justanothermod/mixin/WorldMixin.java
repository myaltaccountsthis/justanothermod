package me.myaltsthis.justanothermod.mixin;

import me.myaltsthis.justanothermod.render.BlockScanner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class WorldMixin {
    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", at = @At(value = "HEAD", target = "Lnet/minecraft/world/World;getWorldChunk(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/chunk/WorldChunk;"))
    private void recheckScan(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
        if (BlockScanner.blocksToRender.isEmpty())
            return;
        BlockScanner.checkBlock(pos, false);
        BlockScanner.checkBlock(pos.add(0, 1, 0), false);
    }
}
