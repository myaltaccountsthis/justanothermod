package me.myaltsthis.justanothermod.mixin;

import me.myaltsthis.justanothermod.MyGameOptions;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
    @Inject(method = "getGamma", at = @At("HEAD"), cancellable = true)
    private void overrideGamma(CallbackInfoReturnable<SimpleOption<Double>> cir) {
        if (MyGameOptions.overrideBrightness.getValue()) {
            cir.setReturnValue(MyGameOptions.brightness);
        }
    }

    @Invoker("isTrue")
    public static boolean isTrue(String value) {
        throw new AssertionError();
    }

    @Invoker("isFalse")
    public static boolean isFalse(String value) {
        throw new AssertionError();
    }
}
