package me.myaltsthis.justanothermod.mixin;

import me.myaltsthis.justanothermod.JustAnotherModClient;
import me.myaltsthis.justanothermod.MyGameOptions;
import me.myaltsthis.justanothermod.hud.PlayerInfoHud;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Shadow private int ticks;

    @Shadow @Final private ChatHud chatHud;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderMountHealth(Lnet/minecraft/client/util/math/MatrixStack;)V"))
    private void injectHud(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (MyGameOptions.showHud.getValue()) PlayerInfoHud.getInstance().render(matrices, tickDelta);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/util/math/MatrixStack;I)V"))
    private void injectPackedHud(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (JustAnotherModClient.loggerActive) {
            matrices.push();
            matrices.translate(0, -this.chatHud.getHeight(), 0);
            JustAnotherModClient.loggerHud.render(matrices, this.ticks); // this.ticks is integer tickDelta
            matrices.pop();
        }
    }
}
