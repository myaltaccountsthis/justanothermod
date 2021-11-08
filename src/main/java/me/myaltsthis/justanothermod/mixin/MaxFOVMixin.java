package me.myaltsthis.justanothermod.mixin;

import me.myaltsthis.justanothermod.client.MyGameOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class MaxFOVMixin {
    @Inject(method = "getSpeed", at = @At("TAIL"), cancellable = true)
    private void setMaxFOV(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(Math.min(cir.getReturnValue(), (float) (MyGameOptions.maxFOV / MinecraftClient.getInstance().options.fov)));
    }
}
