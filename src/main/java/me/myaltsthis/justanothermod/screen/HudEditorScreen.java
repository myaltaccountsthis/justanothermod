package me.myaltsthis.justanothermod.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;

public class HudEditorScreen extends GameOptionsScreen {
    public HudEditorScreen(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @Override
    protected void init() {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 50, this.height / 2 - 10, 100, 10, ScreenTexts.DONE, button -> {

        }));

        super.init();
    }
}
