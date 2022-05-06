package me.myaltsthis.justanothermod.screen;

import me.myaltsthis.justanothermod.MyGameOptions;
import me.myaltsthis.justanothermod.enums.NbtFilter;
import me.myaltsthis.justanothermod.enums.ScanMode;
import me.myaltsthis.justanothermod.render.MonkeyRenderType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TranslatableText;

import java.util.List;

public class MyOptionsScreen extends GameOptionsScreen {
    private static final Option[] OPTIONS;
    private ButtonListWidget list;
    protected TextFieldWidget scanBlockTextField;

    public MyOptionsScreen(Screen screen, GameOptions gameOptions) {
        super(screen, gameOptions, new TranslatableText("justanothermod.optionsScreen"));
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

        this.scanBlockTextField = new TextFieldWidget(this.textRenderer, this.width - 110, this.height - 54, 100, 20, new TranslatableText("justanothermod.options.scanBlockText"));
        this.scanBlockTextField.setText(MyGameOptions.blockToScan);
        this.scanBlockTextField.setChangedListener(text -> MyGameOptions.blockToScan = text);
        this.addSelectableChild(this.scanBlockTextField);

        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, ScreenTexts.DONE, (button) -> {
            MyGameOptions.write();
            //this.client.options.write();
            this.client.setScreen(this.parent);
        }));
        this.addDrawableChild(new ButtonWidget(this.width - 110, this.height - 27, 100, 20, new TranslatableText("justanothermod.options.moreButtons"), (button) -> {
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
        OPTIONS = new Option[] {MyOptions.ENHANCED_MOVEMENT, MyOptions.ALLOW_ELYTRA_BOUNCE, MyOptions.FOG, MyOptions.AUTO_FISH, MyOptions.TRANSPARENT_BACKGROUND, MyOptions.SHOW_HUD, MyOptions.BRIGHTNESS, MyOptions.ZOOM_AMOUNT, MyOptions.MAX_FOV, MyOptions.SCAN_DISTANCE, MyOptions.SCAN_ALPHA_OFFSET, MyOptions.SCAN_LINE_WIDTH, MyOptions.SCAN_MODE, MyOptions.NBT_FILTER};
    }

    private static class MyOptions {
        public static final CyclingOption<Boolean> ENHANCED_MOVEMENT = CyclingOption.create("justanothermod.options.enhancedMovement", gameOptions -> MyGameOptions.enhancedMovement, (gameOptions, option, enableEnhancedMovement) -> MyGameOptions.enhancedMovement = enableEnhancedMovement);
        public static final CyclingOption<Boolean> ALLOW_ELYTRA_BOUNCE = CyclingOption.create("justanothermod.options.elytraBounce", gameOptions -> MyGameOptions.allowElytraBounce, (gameOptions, option, enableElytraBounce) -> MyGameOptions.allowElytraBounce = enableElytraBounce);
        public static final CyclingOption<Boolean> FOG = CyclingOption.create("justanothermod.options.fog", gameOptions -> MyGameOptions.fog, (gameOptions, option, enableFog) -> MyGameOptions.fog = enableFog);
        public static final CyclingOption<Boolean> AUTO_FISH = CyclingOption.create("justanothermod.options.autoFish", gameOptions -> MyGameOptions.autoFish, (gameOptions, option, enableAutoFish) -> MyGameOptions.autoFish = enableAutoFish);
        public static final CyclingOption<Boolean> TRANSPARENT_BACKGROUND = CyclingOption.create("justanothermod.options.transparentBackground", gameOptions -> MyGameOptions.transparentBackground, (gameOptions, option, enableTransparentBackground) -> MyGameOptions.transparentBackground = enableTransparentBackground);
        public static final CyclingOption<Boolean> SHOW_HUD = CyclingOption.create("justanothermod.options.showHud", gameOptions -> MyGameOptions.showHud, (gameOptions, option, enableShowHud) -> MyGameOptions.showHud = enableShowHud);
        public static final DoubleOption BRIGHTNESS = new DoubleOption("customGamma", 0.0D, 10.0D, 0.1F, gameOptions -> gameOptions.gamma, (gameOptions, gamma) -> gameOptions.gamma = gamma, (gameOptions, option) -> new TranslatableText("options.gamma").append(": " + Math.round(option.get(gameOptions) * 100) + "%"));
        public static final DoubleOption ZOOM_AMOUNT = new DoubleOption("justanothermod.options.zoomAmount", 1.0D, 10.0D, 0.1F, gameOptions -> MyGameOptions.zoomAmount, (gameOptions, zoomAmount) -> MyGameOptions.zoomAmount = zoomAmount, (gameOptions, option) -> new TranslatableText("justanothermod.options.zoomAmount").append(": " + Math.round(option.get(gameOptions) * 100) + "%"));
        public static final DoubleOption MAX_FOV = new DoubleOption("justanothermod.options.maxFOV", 30.0D, 360.0D, 1.0F, gameOptions -> MyGameOptions.maxFOV, (gameOptions, maxZoom) -> MyGameOptions.maxFOV = maxZoom, (gameOptions, option) -> new TranslatableText("justanothermod.options.maxFOV").append(": " + Math.round(option.get(gameOptions))));
        public static final DoubleOption SCAN_DISTANCE = new DoubleOption("justanothermod.options.scanDistance", 0.0D, 16.0D, 1.0F, gameOptions -> (double) MyGameOptions.scanDistance, (gameOptions, scanDistance) -> MyGameOptions.scanDistance = scanDistance.intValue(), (gameOptions, option) -> new TranslatableText("justanothermod.options.scanDistance").append(": " + option.get(gameOptions)));
        public static final DoubleOption SCAN_ALPHA_OFFSET = new DoubleOption("justanothermod.options.scanAlphaOffset", -1.0D, 1.0D, 0.1F, gameOptions -> MyGameOptions.scanAlphaOffset, (gameOptions, scanAlphaOffset) -> MyGameOptions.scanAlphaOffset = scanAlphaOffset, (gameOptions, option) -> new TranslatableText("justanothermod.options.scanAlphaOffset").append(": " + Math.round(option.get(gameOptions) * 10) / 10.0d));
        public static final DoubleOption SCAN_LINE_WIDTH = new DoubleOption("justanothermod.options.scanLineWidth", 0.0D, 5.0D, 0.5F, gameOptions -> MyGameOptions.scanLineWidth, (gameOptions, scanLineWidth) -> {
            MyGameOptions.scanLineWidth = scanLineWidth;
            MonkeyRenderType.refreshOverlayLines();
        }, (gameOptions, option) -> new TranslatableText("justanothermod.options.scanLineWidth").append(": " + option.get(gameOptions) + "px"));
        public static final CyclingOption<ScanMode> SCAN_MODE  = CyclingOption.create("justanothermod.options.scanMode", ScanMode.values(), scanMode -> new TranslatableText(scanMode.getTranslationKey()), gameOptions -> MyGameOptions.scanMode, (gameOptions, option, scanMode) -> {
            MyGameOptions.scanMode = scanMode;
        });
        public static final CyclingOption<NbtFilter> NBT_FILTER = CyclingOption.create("justanothermod.options.nbtFilter", NbtFilter.values(), nbtFilter -> new TranslatableText(nbtFilter.getTranslationKey()), gameOptions -> MyGameOptions.nbtFilter, (gameOptions, option, nbtFilter) -> MyGameOptions.nbtFilter = nbtFilter);
    }
}
