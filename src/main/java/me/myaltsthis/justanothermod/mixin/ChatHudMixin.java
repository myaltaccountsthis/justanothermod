package me.myaltsthis.justanothermod.mixin;

import me.myaltsthis.justanothermod.JustAnotherModClient;
import me.myaltsthis.justanothermod.hud.LoggerHud;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Inject(method = "isChatFocused", at = @At("HEAD"), cancellable = true)
    private void removeChatFocusedForPacket(CallbackInfoReturnable<Boolean> cir) {
        if (this.getClass().getName().equals(LoggerHud.class.getName())) {
            cir.setReturnValue(JustAnotherModClient.loggerActive);
        }
    }

    @Redirect(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V"))
    private void doNotLogForPacket(org.slf4j.Logger instance, String s, Object o) {
        if (!this.getClass().getName().equals(LoggerHud.class.getName())) {
            instance.info(s, o);
        }
    }
}
