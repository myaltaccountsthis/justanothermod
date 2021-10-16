package me.myaltsthis.justanothermod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class JustAnotherModClient implements ClientModInitializer {
    private static ButtonWidget toggleButton;
    private static boolean enabled = false;

    public static void setToggleButton(ButtonWidget btn) {
        toggleButton = btn;
    }
    public static void setToggleButtonText(String text) {
        toggleButton.setMessage(new TranslatableText(text));
        enabled = text.equals("Enabled");
    }
    public static boolean isButtonEnabled() {
        return enabled;
    }

    @Override
    public void onInitializeClient() {

        System.out.println("loaded");
    }
}
