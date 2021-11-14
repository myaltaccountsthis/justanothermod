package me.myaltsthis.justanothermod.mixin;

import me.myaltsthis.justanothermod.JustAnotherModClient;
import net.minecraft.client.option.DoubleOption;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(GameOptions.class)
public class OptionsMixin {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/DoubleOption;setMax(F)V"))
    private void changeMaxRenderDistance(DoubleOption instance, float max) {
        instance.setMax(64);
        JustAnotherModClient.LOGGER.info("set max");
    }
}