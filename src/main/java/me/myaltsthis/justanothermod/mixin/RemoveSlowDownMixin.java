package me.myaltsthis.justanothermod.mixin;

import com.mojang.authlib.GameProfile;
import me.myaltsthis.justanothermod.client.JustAnotherModClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class RemoveSlowDownMixin extends AbstractClientPlayerEntity {

    public RemoveSlowDownMixin(ClientWorld clientWorld, GameProfile gameProfile) {
        super(clientWorld, gameProfile);
    }

    @Inject(at = @At("HEAD"), method = "shouldSlowDown", cancellable = true)
    private void changeReturn(CallbackInfoReturnable<Boolean> cir) {
        if (JustAnotherModClient.isButtonEnabled())
            cir.setReturnValue(false);
    }
}
