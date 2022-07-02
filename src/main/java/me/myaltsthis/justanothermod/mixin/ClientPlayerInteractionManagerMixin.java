package me.myaltsthis.justanothermod.mixin;

import me.myaltsthis.justanothermod.JustAnotherModClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(method = "interactBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V"))
    private void checkStack(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (JustAnotherModClient.infinitePlace) {
            ItemStack itemStack = player.getStackInHand(hand);
            if (!itemStack.isEmpty() && itemStack.isStackable() && itemStack.getCount() < 2 && !player.isCreative()) {
                Item type = itemStack.getItem();
                //MinecraftClient.getInstance().doItemUse();
                // check hotbar
                PlayerInventory inventory = player.getInventory();
                for (int i = 0; i < 9; i++) {
                    ItemStack item = inventory.getStack(i);
                    if (item.isOf(type) && item != itemStack) {
                        inventory.selectedSlot = i;
                        return;
                    }
                }
            }
        }
    }
}
