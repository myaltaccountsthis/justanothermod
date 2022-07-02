package me.myaltsthis.justanothermod.screen;

import me.myaltsthis.justanothermod.MyGameOptions;
import me.myaltsthis.justanothermod.render.BlockScanner;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class MyButtonScreen extends GameOptionsScreen {

    public MyButtonScreen(Screen screen, GameOptions gameOptions) {
        super(screen, gameOptions, Text.translatable("justanothermod.buttonScreen"));
    }

    @Override
    protected void init() {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height / 6, 150, 20, Text.translatable("justanothermod.options.setMaxFOV"), (button) -> MyGameOptions.maxFOV = MinecraftClient.getInstance().options.getFov().getValue() * 1.15));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, this.height / 6, 150, 20, Text.translatable("justanothermod.key.refreshScan"), (button) -> Util.getMainWorkerExecutor().execute(new BlockScanner(true))));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height / 6 + 24, 150, 20, Text.translatable("justanothermod.key.clearScan"), (button) -> BlockScanner.blocksToRender.clear()));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, this.height / 6 + 24, 150, 20, Text.translatable("justanothermod.key.samePosScan"), (button) -> Util.getMainWorkerExecutor().execute(new BlockScanner(false))));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 168, 200, 20, ScreenTexts.DONE, button -> this.client.setScreen(this.parent)));

        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
