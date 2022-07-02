package me.myaltsthis.justanothermod.mixin;

import me.myaltsthis.justanothermod.JustAnotherModClient;
import me.myaltsthis.justanothermod.MyGameOptions;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {

    @Shadow private HitResult blockHit;

    @Inject(method = "getLeftText", at = @At("TAIL"))
    private void addBlockNbt(CallbackInfoReturnable<List<String>> cir) {
        if (MinecraftClient.getInstance().world != null) {
            List<String> list = cir.getReturnValue();
            boolean pressed = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), MyGameOptions.keyShowTooltip.boundKey.getCode());
            ArrayList<String> toAdd = new ArrayList<>();
            { // blocks
                BlockPos pos = ((BlockHitResult) blockHit).getBlockPos();
                BlockEntity blockEntity = MinecraftClient.getInstance().world.getBlockEntity(pos);
                if (blockEntity != null) {
                    toAdd.add("Block NBT");
                    if (pressed) {
                        NbtCompound nbt = blockEntity.createNbt();
                        nbt.remove("id");
                        nbt.remove("x");
                        nbt.remove("y");
                        nbt.remove("z");
                        String nbtStr = JustAnotherModClient.toPrettyFormat(nbt.toString());
                        toAdd.addAll(Arrays.stream(nbtStr.split("\n")).toList());
                    } else {
                        toAdd.add(Text.translatable("justanothermod.tooltipDefault").getString().replace("key", MyGameOptions.keyShowTooltip.getBoundKeyLocalizedText().getString()));
                    }
                }
            }
            { // entities
                Entity entity = MinecraftClient.getInstance().targetedEntity;
                if (entity != null) {
                    toAdd.add("Entity NBT");
                    if (pressed) {
                        NbtCompound nbt = JustAnotherModClient.getEntityNbt();
                        String nbtStr = JustAnotherModClient.toPrettyFormat(nbt.toString());
                        toAdd.addAll(List.of(nbtStr.split("\n")));
                    } else {
                        toAdd.add(Text.translatable("justanothermod.tooltipDefault").getString().replace("key", MyGameOptions.keyShowTooltip.getBoundKeyLocalizedText().getString()));
                    }
                }
            }
            if (!toAdd.isEmpty()) {
                toAdd.add(0, "");
                list.addAll(toAdd);
            }
        }
    }
}