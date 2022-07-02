package me.myaltsthis.justanothermod.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {
    /*
    @Inject(method = "getSpeed", at = @At("TAIL"), cancellable = true)
    private void setMaxFOV(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(Math.min(cir.getReturnValue(), (float) (MyGameOptions.maxFOV / MinecraftClient.getInstance().options.fov)));
    }
     */
}
