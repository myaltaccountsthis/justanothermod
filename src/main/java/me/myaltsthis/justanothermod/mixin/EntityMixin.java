package me.myaltsthis.justanothermod.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.myaltsthis.justanothermod.JustAnotherModClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract Vec3d getVelocity();

    @Shadow public abstract void setVelocity(Vec3d velocity);

    @Shadow
    private static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
        return null;
    }

    @Shadow public abstract float getYaw();

    @Inject(method = "updateVelocity", at = @At("TAIL"))
    private void spy(float speed, Vec3d movementInput, CallbackInfo ci) {
        if (RenderSystem.isOnRenderThread()) {
            Vec3d oldVelocity = getVelocity();
            /*
            speed = Math.max(speed, .1f);
            speed = speed * 4.6f / 2;
            setVelocity(movementInputToVelocity(movementInput, speed, getYaw()).add(0, oldVelocity.y, 0));
             */
            //JustAnotherModClient.LOGGER.info("Speed: " + speed + ", V: " + getVelocity().horizontalLength() + ", V3d: " + movementInput);
        }
    }
}
