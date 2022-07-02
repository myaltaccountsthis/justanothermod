package me.myaltsthis.justanothermod.mixin;

import me.myaltsthis.justanothermod.MyGameOptions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {
    @Inject(method = "renderBackground(Lnet/minecraft/client/util/math/MatrixStack;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;fillGradient(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"), cancellable = true)
    private void doNotRenderBackground(MatrixStack matrices, int vOffset, CallbackInfo ci) {
        if (MyGameOptions.transparentBackground.getValue()) ci.cancel();
    }
}
