package me.myaltsthis.justanothermod.mixin;

import me.myaltsthis.justanothermod.JustAnotherModClient;
import me.myaltsthis.justanothermod.MyGameOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin {
    @Shadow private boolean caughtFish;

    @Shadow private int hookCountdown;

    @Shadow public abstract @Nullable PlayerEntity getPlayerOwner();

    // add setting and time delay
    @Inject(method = "onTrackedDataSet", at = @At("TAIL"))
    private void checkCaughtFish(TrackedData<?> data, CallbackInfo ci) {
        if (MyGameOptions.autoFish) {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                if (player.equals(this.getPlayerOwner()) && this.caughtFish && this.hookCountdown == 0) {
                    if (MinecraftClient.getInstance().interactionManager != null) {
                        // catch and get
                        for (int i = 0; i < 2; i++)
                            MinecraftClient.getInstance().doItemUse();
                    }
                }
            }
        }
    }
}
