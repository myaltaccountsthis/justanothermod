package me.myaltsthis.justanothermod.mixin;

import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkBuilder.BuiltChunk.class)
public class ChunkBuilderMixin {
    /*
    @Redirect(
            method = "shouldBuild",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/chunk/ChunkBuilder$BuiltChunk;getSquaredCameraDistance()D")
    )
    private double modifyMaxDistance(ChunkBuilder.BuiltChunk instance) {
        return 0;
    }

    @Inject(method = "shouldBuild", at = @At("HEAD"))
    private void testing(CallbackInfoReturnable<Boolean> cir) {
        System.out.println("works");
    }
     */
}
