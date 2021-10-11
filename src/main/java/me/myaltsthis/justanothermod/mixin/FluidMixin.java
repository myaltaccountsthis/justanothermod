package me.myaltsthis.justanothermod.mixin;

import me.myaltsthis.justanothermod.client.JustAnotherModClient;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(LivingEntity.class)
public class FluidMixin {
    @ModifyVariable(method = "travel", at = @At("STORE"), ordinal = 2)
    private float modifyH(float h) {
        if (JustAnotherModClient.isButtonEnabled()) {
            return 3F;
        }
        return h;
    }

    @ModifyConstant(method = "travel", constant = @Constant(floatValue = 0.02F), slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isInLava()Z")
    ))
    private float modifyConstant(float f) {
        if (JustAnotherModClient.isButtonEnabled()) {
            //System.out.println("f: " + f);
            return .1F;
        }
        return f;
    }
}
