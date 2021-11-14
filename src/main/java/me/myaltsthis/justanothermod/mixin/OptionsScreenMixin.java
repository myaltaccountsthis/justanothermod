package me.myaltsthis.justanothermod.mixin;

import me.myaltsthis.justanothermod.MyGameOptions;
import me.myaltsthis.justanothermod.MyOptionsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.DoubleOption;
import net.minecraft.client.option.Option;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {
    @Shadow @Final private static Option[] OPTIONS;

    protected OptionsScreenMixin(Text text) {
        super(text);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void addCustomButton(CallbackInfo ci) {
        // button for menu
        {
            ButtonWidget button = new ButtonWidget(this.width / 2 - 49, this.height / 6 + 168 + (-20 - 4), 98, 20, new TranslatableText("Open Menu"), (btn) -> {
                this.client.setScreen(new MyOptionsScreen(this.client.currentScreen, this.client.options));
            });
            this.addDrawableChild(button);
        }
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void changeMaxFOV(CallbackInfo ci) {
        DoubleOption doubleOption = ((DoubleOption) OPTIONS[0]);
        double d = MyGameOptions.maxFOV / 1.15;
        doubleOption.setMax((float) d);
        doubleOption.set(MinecraftClient.getInstance().options, Math.min(doubleOption.get(MinecraftClient.getInstance().options), d));
    }
}
