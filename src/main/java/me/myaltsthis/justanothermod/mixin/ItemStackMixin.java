package me.myaltsthis.justanothermod.mixin;

import me.myaltsthis.justanothermod.JustAnotherModClient;
import me.myaltsthis.justanothermod.MyGameOptions;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
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

import java.util.ArrayList;
import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow private NbtCompound nbt;

    @Inject(method = "getTooltip", at = @At("TAIL"))
    private void addTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        List<Text> list = cir.getReturnValue();
        LiteralText text = null;
        if (nbt != null) {
            NbtCompound copy = nbt.copy();
            if (MinecraftClient.getInstance().player != null) {
                if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_CONTROL)) {
                    String toCopy = JustAnotherModClient.toPrettyFormat(copy.toString());
                    if (!toCopy.equals(MinecraftClient.getInstance().keyboard.getClipboard())) {
                        MinecraftClient.getInstance().keyboard.setClipboard(toCopy);
                        MinecraftClient.getInstance().player.sendSystemMessage(new LiteralText("Copied item NBT to clipboard").formatted(Formatting.GREEN), null);
                    }
                }
            }
            if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), MyGameOptions.keyShowTooltip.boundKey.getCode())) {
                // compact shulker
                NbtList nbtList = copy.getCompound("BlockEntityTag").getList("Items", NbtElement.COMPOUND_TYPE);
                if (!nbtList.isEmpty()) {
                    ArrayList<String> newList = new ArrayList<>();
                    for (NbtElement element : nbtList) {
                        NbtCompound compound = ((NbtCompound) element);
                        newList.add("%02d".formatted(compound.getByte("Slot")) + ": " + "%02d".formatted(compound.getByte("Count")) + " " + compound.getString("id"));
                    }
                    nbtList.clear();
                    for (String str : newList) {
                        nbtList.add(NbtString.of(str));
                    }
                }
                String nbtStr = JustAnotherModClient.toPrettyFormat(copy.toString());
                for (String s : nbtStr.split("\n")) {
                    list.add(new LiteralText(s).formatted(Formatting.DARK_GRAY));
                }
                return;
            } else {
                text = new LiteralText(new TranslatableText("justanothermod.tooltipDefault").getString().replace("key", MyGameOptions.keyShowTooltip.getBoundKeyLocalizedText().getString()));
            }
        }
        if (text != null)
            list.add(text.formatted(Formatting.DARK_GRAY));
    }
}
