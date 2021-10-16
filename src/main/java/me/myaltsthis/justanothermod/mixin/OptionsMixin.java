package me.myaltsthis.justanothermod.mixin;

import net.minecraft.client.option.DoubleOption;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameOptions.class)
public class OptionsMixin {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/DoubleOption;setMax(F)V"))
    private void changeMaxRenderDistance(DoubleOption instance, float max) {
        System.out.println("max is uh: " + max);
        instance.setMax(64);
        System.out.println("now it is: " + instance.getMax());
    }
}
