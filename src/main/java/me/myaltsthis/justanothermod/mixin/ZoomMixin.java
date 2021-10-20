package me.myaltsthis.justanothermod.mixin;

import me.myaltsthis.justanothermod.client.MyGameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class ZoomMixin {

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void modifyFOV(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        if (changingFov && (!((GameRenderer) (Object) this).isRenderingPanorama())) {
            if (MyGameOptions.keyZoom.isPressed()) {
                cir.setReturnValue(cir.getReturnValueD() / MyGameOptions.zoomAmount);
            }
        }
    }
}
