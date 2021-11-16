package me.myaltsthis.justanothermod.mixin;

import me.myaltsthis.justanothermod.JustAnotherModClient;
import me.myaltsthis.justanothermod.MyGameOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow private NbtCompound nbt;

    @Inject(method = "getTooltip", at = @At("TAIL"))
    private void addTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        List<Text> list = cir.getReturnValue();
        LiteralText text;
        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), MyGameOptions.keyShowTooltip.boundKey.getCode())) {
            if (nbt != null) {
                String nbtStr = JustAnotherModClient.toPrettyFormat(nbt.toString());
                for (String s : nbtStr.split("\n")) {
                    list.add(new LiteralText(s).formatted(Formatting.DARK_GRAY));
                }
                return;
            } else {
                text = new LiteralText("NBT: no nbt");
            }
        } else {
            text = new LiteralText(new TranslatableText("justanothermod.tooltipDefault").getString().replace("key", MyGameOptions.keyShowTooltip.getBoundKeyLocalizedText().getString()));
        }
        list.add(text.formatted(Formatting.DARK_GRAY));
    }
}
