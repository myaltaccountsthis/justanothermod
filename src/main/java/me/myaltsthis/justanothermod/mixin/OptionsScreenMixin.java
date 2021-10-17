package me.myaltsthis.justanothermod.mixin;

import me.myaltsthis.justanothermod.client.MyOptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {
    protected OptionsScreenMixin(Text text) {
        super(text);
    }

    @Inject(at = @At("RETURN"), method = "init")
    private void addCustomButton(CallbackInfo ci) {
        // button for menu
        {
            ButtonWidget button = new ButtonWidget(this.width / 2 - 49, this.height / 6 + 168 + (-20 - 4), 98, 20, new TranslatableText("Open Menu"), (btn) -> {
                this.client.setScreen(new MyOptionsScreen(this.client.currentScreen, this.client.options));
            });
            this.addDrawableChild(button);
        }
    }
}
