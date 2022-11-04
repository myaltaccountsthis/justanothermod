package me.myaltsthis.justanothermod.mixin;

import me.myaltsthis.justanothermod.JustAnotherModClient;
import me.myaltsthis.justanothermod.hud.LoggerHud;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Inject(method = "isChatFocused", at = @At("HEAD"), cancellable = true)
    private void removeChatFocusedForPacket(CallbackInfoReturnable<Boolean> cir) {
        if (this.getClass().getName().equals(LoggerHud.class.getName())) {
            cir.setReturnValue(JustAnotherModClient.loggerActive);
        }
    }

    @Inject(method = "logChatMessage", at = @At("HEAD"), cancellable = true)
    private void doNotLogForPacket(Text message, MessageIndicator indicator, CallbackInfo ci) {
        if (this.getClass().getName().equals(LoggerHud.class.getName())) {
            ci.cancel();
        }
    }
}
