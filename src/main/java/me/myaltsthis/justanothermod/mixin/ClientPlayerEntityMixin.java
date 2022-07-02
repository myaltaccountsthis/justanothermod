package me.myaltsthis.justanothermod.mixin;

import me.myaltsthis.justanothermod.JustAnotherModClient;
import me.myaltsthis.justanothermod.MyGameOptions;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    private static final Logger LOGGER = JustAnotherModClient.LOGGER;

    @Shadow public Input input;

    @Shadow @Final public ClientPlayNetworkHandler networkHandler;

    private boolean isEnabled() {
        return MyGameOptions.enhancedMovement.getValue();
    }

    @Inject(method = "shouldSlowDown", at = @At("HEAD"), cancellable = true)
    private void changeReturn(CallbackInfoReturnable<Boolean> cir) {
        if (isEnabled())
            cir.setReturnValue(false);
    }

    @Redirect(
            method = "tickMovement",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"),
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/client/input/Input;jumping:Z"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;ticksToNextAutojump:I")
            )
    )
    private boolean notUsingItem(ClientPlayerEntity instance) {
        if (isEnabled())
            return false;
        return instance.isUsingItem();
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tickMovement()V"))
    private void injectedSprint(CallbackInfo ci) {

        if (isEnabled()) {
            ClientPlayerEntity player = ((ClientPlayerEntity) (Object) this);
            if (player.input.movementForward != 0 || player.input.movementSideways != 0) {
                player.setSprinting(true);
            }
        }
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isFallFlying()Z", shift = At.Shift.AFTER))
    private void forceFly(CallbackInfo ci) {
        if (MyGameOptions.allowElytraBounce.getValue()) {
            ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity) (Object) this;
            if (!clientPlayerEntity.isFallFlying() && JustAnotherModClient.jumpNextTick && clientPlayerEntity.checkFallFlying()) {
                JustAnotherModClient.jumpNextTick = false;
                networkHandler.sendPacket(new ClientCommandC2SPacket(clientPlayerEntity, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            }
        }
    }
}

