package me.myaltsthis.justanothermod.mixin;

import me.myaltsthis.justanothermod.client.JustAnotherModClient;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class FluidMixin {
    @Shadow public float flyingSpeed;

    @ModifyVariable(method = "travel", at = @At("STORE"), ordinal = 2)
    private float modifyWater(float h) {
        if (JustAnotherModClient.isButtonEnabled() && ((LivingEntity)(Object) this).getClass().getSimpleName().equals("ClientPlayerEntity"))
            return 3F;
        return h;
    }

    @ModifyConstant(method = "travel", constant = @Constant(floatValue = 0.02F), slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isInLava()Z")
    ))
    private float modifyLava(float f) {
        if (JustAnotherModClient.isButtonEnabled() && ((LivingEntity)(Object) this).getClass().getSimpleName().equals("ClientPlayerEntity"))
            return .15F;
        return f;
    }
    /*
    @Inject(method = "getMovementSpeed(F)F", at = @At("RETURN"), cancellable = true)
    private void modifyMovementSpeed(float slipperiness, CallbackInfoReturnable<Float> cir) {
        LivingEntity e = (LivingEntity) (Object) this;
        if (JustAnotherModClient.isButtonEnabled() && e.getClass().getSimpleName().equals("ClientPlayerEntity")) {

            cir.setReturnValue(e.isOnGround() ? e.getMovementSpeed() : flyingSpeed);
        }
    }
     */
}
