package me.myaltsthis.justanothermod.screen;

import me.myaltsthis.justanothermod.MyGameOptions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

public class MyOptionsScreen extends GameOptionsScreen {
    private static final SimpleOption[] OPTIONS;
    private ButtonListWidget list;
    protected TextFieldWidget scanBlockTextField;

    public MyOptionsScreen(Screen screen, GameOptions gameOptions) {
        super(screen, gameOptions, Text.translatable("justanothermod.optionsScreen"));
    }

    @Override
    protected void init() {
        this.client.keyboard.setRepeatEvents(true);
        // client, width, height, top, bottom, itemHeight
        this.list = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 60, 25);
        this.list.setRenderBackground(false);
        this.list.setRenderHorizontalShadows(false);
        this.list.addAll(OPTIONS);
        this.addSelectableChild(this.list);

        this.scanBlockTextField = new TextFieldWidget(this.textRenderer, this.width - 110, this.height - 54, 100, 20, Text.translatable("justanothermod.options.scanBlockText"));
        this.scanBlockTextField.setText(MyGameOptions.blockToScan);
        this.scanBlockTextField.setChangedListener(text -> MyGameOptions.blockToScan = text);
        this.addSelectableChild(this.scanBlockTextField);

        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, ScreenTexts.DONE, (button) -> {
            MyGameOptions.write();
            //this.client.options.write();
            this.client.setScreen(this.parent);
        }));
        this.addDrawableChild(new ButtonWidget(this.width - 110, this.height - 27, 100, 20, Text.translatable("justanothermod.options.moreButtons"), (button) -> {
            MyGameOptions.write();
            this.client.setScreen(new MyButtonScreen(this, this.gameOptions));
        }));
        super.init();
    }

    @Override
    public void tick() {
        this.scanBlockTextField.tick();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.scanBlockTextField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return false;
    }

    public void removed() {
        MyGameOptions.write();
        this.client.options.write();
        super.removed();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.list.render(matrices, mouseX, mouseY, delta);
        this.scanBlockTextField.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 5, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
        List<OrderedText> list = getHoveredButtonTooltip(this.list, mouseX, mouseY);
        if (list != null) {
            this.renderOrderedTooltip(matrices, list, mouseX, mouseY);
        }
    }

    static {
        OPTIONS = new SimpleOption[] {MyGameOptions.enhancedMovement, MyGameOptions.allowElytraBounce, MyGameOptions.fog, MyGameOptions.autoFish, MyGameOptions.transparentBackground, MyGameOptions.showHud, MyGameOptions.airMovement, MyGameOptions.overrideBrightness, MyGameOptions.brightness, MyGameOptions.zoomAmount, /*MyGameOptions.MAX_FOV,*/ MyGameOptions.scanDistance, MyGameOptions.scanAlphaOffset, MyGameOptions.scanLineWidth, MyGameOptions.scanMode, MyGameOptions.nbtFilter};
    }
}
