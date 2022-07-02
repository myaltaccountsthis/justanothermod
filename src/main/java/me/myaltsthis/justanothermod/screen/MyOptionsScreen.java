package me.myaltsthis.justanothermod.screen;

import com.mojang.serialization.Codec;
import me.myaltsthis.justanothermod.MyGameOptions;
import me.myaltsthis.justanothermod.enums.NbtFilter;
import me.myaltsthis.justanothermod.enums.ScanMode;
import me.myaltsthis.justanothermod.render.MonkeyRenderType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.Arrays;
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

    public static Text getGenericValueText(Text prefix, Object value) {
        return Text.translatable("justanothermod.options.generic_value", prefix, Text.literal(value.toString()));
    }


    static {
        OPTIONS = new SimpleOption[] {MyOptions.ENHANCED_MOVEMENT, MyOptions.ALLOW_ELYTRA_BOUNCE, MyOptions.FOG, MyOptions.AUTO_FISH, MyOptions.TRANSPARENT_BACKGROUND, MyOptions.SHOW_HUD, /*MyOptions.BRIGHTNESS,*/ MyOptions.ZOOM_AMOUNT, /*MyOptions.MAX_FOV,*/ MyOptions.SCAN_DISTANCE, MyOptions.SCAN_ALPHA_OFFSET, MyOptions.SCAN_LINE_WIDTH, MyOptions.SCAN_MODE, MyOptions.NBT_FILTER};
    }

    private static class MyOptions {
        public static final SimpleOption<Boolean> ENHANCED_MOVEMENT = SimpleOption.ofBoolean("justanothermod.options.enhancedMovement", false, enableEnhancedMovement -> MyGameOptions.enhancedMovement = enableEnhancedMovement);
        public static final SimpleOption<Boolean> ALLOW_ELYTRA_BOUNCE = SimpleOption.ofBoolean("justanothermod.options.elytraBounce", false, enableElytraBounce -> MyGameOptions.allowElytraBounce = enableElytraBounce);
        public static final SimpleOption<Boolean> FOG = SimpleOption.ofBoolean("justanothermod.options.fog", false, enableFog -> MyGameOptions.fog = enableFog);
        public static final SimpleOption<Boolean> AUTO_FISH = SimpleOption.ofBoolean("justanothermod.options.autoFish", false, enableAutoFish -> MyGameOptions.autoFish = enableAutoFish);
        public static final SimpleOption<Boolean> TRANSPARENT_BACKGROUND = SimpleOption.ofBoolean("justanothermod.options.transparentBackground", false, enableTransparentBackground -> MyGameOptions.transparentBackground = enableTransparentBackground);
        public static final SimpleOption<Boolean> SHOW_HUD = SimpleOption.ofBoolean("justanothermod.options.showHud", false, enableShowHud -> MyGameOptions.showHud = enableShowHud);
        //public static final SimpleOption<Double> BRIGHTNESS = new SimpleOption<Double>("customGamma", SimpleOption.emptyTooltip(), (prefix, value) -> Text.translatable("options.percent_value", prefix, (int)(value * 100.0)), 0.0D, 10.0D, 0.1F, gameOptions -> gameOptions.gamma, (gameOptions, gamma) -> gameOptions.gamma = gamma, (gameOptions, option) -> Text.translatable("options.gamma")).append(": " + Math.round(option.get(gameOptions) * 100) + "%"));
        public static final SimpleOption<Double> ZOOM_AMOUNT = new SimpleOption<>("justanothermod.options.zoomAmount",  SimpleOption.emptyTooltip(), GameOptions::getPercentValueText, new SimpleOption.ValidatingIntSliderCallbacks(10, 100).withModifier(sliderProgressValue -> sliderProgressValue / 10.0, value -> (int) (value * 10)), Codec.doubleRange(1.0, 10.0), 4.0, zoomAmount -> MyGameOptions.zoomAmount = zoomAmount);
        //public static final SimpleOption<Double> MAX_FOV = new SimpleOption<Double>("justanothermod.options.maxFOV", 30.0D, 360.0D, 1.0F, gameOptions -> MyGameOptions.maxFOV, (gameOptions, maxZoom) -> MyGameOptions.maxFOV = maxZoom, (gameOptions, option) -> Text.translatable("justanothermod.options.maxFOV")).append(": " + Math.round(option.get(gameOptions))));
        public static final SimpleOption<Integer> SCAN_DISTANCE = new SimpleOption<>("justanothermod.options.scanDistance", SimpleOption.emptyTooltip(), GameOptions::getGenericValueText, new SimpleOption.ValidatingIntSliderCallbacks(0, 8), Codec.intRange(0, 8), 4, scanDistance -> MyGameOptions.scanDistance = scanDistance);
        public static final SimpleOption<Double> SCAN_ALPHA_OFFSET = new SimpleOption<>("justanothermod.options.scanAlphaOffset", SimpleOption.emptyTooltip(), MyOptionsScreen::getGenericValueText, new SimpleOption.ValidatingIntSliderCallbacks(-10, 10).withModifier(sliderProgressValue -> sliderProgressValue / 10.0, value -> (int) (value * 10)), Codec.doubleRange(-1.0D, 1.0D), -.3, scanAlphaOffset -> MyGameOptions.scanAlphaOffset = scanAlphaOffset);
        public static final SimpleOption<Double> SCAN_LINE_WIDTH = new SimpleOption<>("justanothermod.options.scanLineWidth", SimpleOption.emptyTooltip(), (prefix, value) -> MutableText.of(MyOptionsScreen.getGenericValueText(prefix, value).getContent()).append("px"), new SimpleOption.ValidatingIntSliderCallbacks(0, 10).withModifier(sliderProgressValue -> sliderProgressValue / 2.0, value -> (int) (value * 2)), Codec.doubleRange(0.0D, 5.0D), 3.0, scanLineWidth -> {
            MyGameOptions.scanLineWidth = scanLineWidth;
            MonkeyRenderType.refreshOverlayLines();
        });
        public static final SimpleOption<ScanMode> SCAN_MODE  = new SimpleOption<>("justanothermod.options.scanMode", SimpleOption.emptyTooltip(), SimpleOption.enumValueText(), new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(ScanMode.values()), Codec.INT.xmap(ScanMode::byId, ScanMode::getId)), ScanMode.MOB_SPAWN, scanMode -> MyGameOptions.scanMode = scanMode);
        public static final SimpleOption<NbtFilter> NBT_FILTER = new SimpleOption<NbtFilter>("justanothermod.options.nbtFilter", SimpleOption.emptyTooltip(), SimpleOption.enumValueText(),new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(NbtFilter.values()), Codec.INT.xmap(NbtFilter::byId, NbtFilter::getId)), NbtFilter.BASIC, nbtFilter -> MyGameOptions.nbtFilter = nbtFilter);
    }
}
