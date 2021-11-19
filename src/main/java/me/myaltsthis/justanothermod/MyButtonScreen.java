package me.myaltsthis.justanothermod;

import me.myaltsthis.justanothermod.render.BlockScanner;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.TranslatableText;

public class MyButtonScreen extends GameOptionsScreen {

    public MyButtonScreen(Screen screen, GameOptions gameOptions) {
        super(screen, gameOptions, new TranslatableText("justanothermod.buttonScreen"));
    }

    protected void init() {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height / 6, 150, 20, new TranslatableText("justanothermod.options.setMaxFOV"), (button) -> MyGameOptions.maxFOV = MinecraftClient.getInstance().options.fov * 1.15));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, this.height / 6, 150, 20, new TranslatableText("justanothermod.key.refreshScan"), (button) -> BlockScanner.run()));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height / 6 + 24, 150, 20, new TranslatableText("justanothermod.key.clearScan"), (button) -> BlockScanner.blocksToRender.clear()));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 168, 200, 20, ScreenTexts.DONE, button -> this.client.setScreen(this.parent)));

        super.init();
    }
}
