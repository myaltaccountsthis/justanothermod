package me.myaltsthis.justanothermod.mixin;

import me.myaltsthis.justanothermod.client.JustAnotherModClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Text text) {
        super(text);
    }

    @Inject(at = @At("RETURN"), method = "initWidgets")
    private void addCustomButton(CallbackInfo ci) {
        ButtonWidget button = new ButtonWidget(this.width - 4 - 98, this.height - 4 - 20, 98, 20, new TranslatableText(JustAnotherModClient.isButtonEnabled() ? "Enabled" : "Disabled"), (btn) -> {
            JustAnotherModClient.setToggleButtonText(btn.getMessage().getString().equals("Enabled") ? "Disabled" : "Enabled");
            System.out.println("Toggled " + btn.getMessage().getString());
        });
        JustAnotherModClient.setToggleButton(button);
        this.addDrawableChild(button);
    }
}
