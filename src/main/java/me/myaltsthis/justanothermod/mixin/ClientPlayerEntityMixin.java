package me.myaltsthis.justanothermod.mixin;

import me.myaltsthis.justanothermod.JustAnotherModClient;
import me.myaltsthis.justanothermod.MyGameOptions;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.AnnotationNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.util.Annotations;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    private static final Logger LOGGER = JustAnotherModClient.LOGGER;

    @Shadow public Input input;

    private boolean isEnabled() {
        return MyGameOptions.enhancedMovement;
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

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isClimbing()Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void autoElytra(CallbackInfo ci, boolean bl, boolean bl8) {
        boolean jumping = input.jumping;
        boolean flying = ((ClientPlayerEntity) (Object) this).getAbilities().flying;
        boolean hasVehicle = ((ClientPlayerEntity) (Object) this).hasVehicle();
        boolean isClimbing = ((ClientPlayerEntity) (Object) this).isClimbing();
        ItemStack itemStack = ((ClientPlayerEntity) (Object) this).getEquippedStack(EquipmentSlot.CHEST);
        boolean usingElytra = itemStack.isOf(Items.ELYTRA);
        boolean checkFallFlying = ((ClientPlayerEntity) (Object) this).checkFallFlying();
        LOGGER.info("jumping: " + jumping + ", flying: " + flying + ", hasVehicle: " + hasVehicle + ", isClimbing: " + isClimbing + ", usingElytra: " + usingElytra + ", checkFallFlying: " + checkFallFlying);
        if (jumping && !bl8 && !bl && !flying && !hasVehicle && !isClimbing && usingElytra && ElytraItem.isUsable(itemStack) && checkFallFlying) {
            LOGGER.info("can use");
        }
    }
}

