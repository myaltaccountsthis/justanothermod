package me.myaltsthis.justanothermod.mixin;

import me.myaltsthis.justanothermod.JustAnotherModClient;
import me.myaltsthis.justanothermod.MyGameOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow protected abstract void jump();

    @Shadow public abstract boolean isFallFlying();

    @ModifyVariable(method = "travel", at = @At("STORE"), ordinal = 2)
    private float modifyWater(float h) {
        if (MyGameOptions.enhancedMovement.getValue() && ((LivingEntity)(Object) this).getClass().getSimpleName().equals("ClientPlayerEntity"))
            return 3F;
        return h;
    }

    @ModifyConstant(method = "travel", constant = @Constant(floatValue = 0.02F), slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isInLava()Z")
    ))
    private float modifyLava(float f) {
        if (MyGameOptions.enhancedMovement.getValue() && ((LivingEntity)(Object) this).getClass().getSimpleName().equals("ClientPlayerEntity"))
            return .15F;
        return f;
    }

    @Redirect(method = "travel", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isClient:Z"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setFlag(IZ)V")))
    private boolean modify(World world) {
        return false;
    }

    @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setFlag(IZ)V", shift = At.Shift.AFTER))
    private void autoElytra(Vec3d movementInput, CallbackInfo ci) {
        if (MyGameOptions.allowElytraBounce.getValue()) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null && player.equals((LivingEntity) (Object) this)) {
                if (MinecraftClient.getInstance().options.jumpKey.isPressed()) {
                    player.jump();
                    JustAnotherModClient.jumpNextTick = true;
                }
            }
        }
    }
}
