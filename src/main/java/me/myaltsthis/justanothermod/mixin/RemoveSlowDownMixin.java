package me.myaltsthis.justanothermod.mixin;

import com.mojang.authlib.GameProfile;
import me.myaltsthis.justanothermod.client.JustAnotherModClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class RemoveSlowDownMixin extends AbstractClientPlayerEntity {

    public RemoveSlowDownMixin(ClientWorld clientWorld, GameProfile gameProfile) {
        super(clientWorld, gameProfile);
    }

    @Inject(method = "shouldSlowDown", at = @At("HEAD"), cancellable = true)
    private void changeReturn(CallbackInfoReturnable<Boolean> cir) {
        if (JustAnotherModClient.isButtonEnabled())
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
        if (JustAnotherModClient.isButtonEnabled())
            return false;
        return instance.isUsingItem();
    }
}

