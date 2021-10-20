package me.myaltsthis.justanothermod.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;

import java.security.Key;
import java.util.List;

public class MyOptionsScreen extends GameOptionsScreen {
    private static final Option[] OPTIONS;
    private ButtonListWidget list;

    public MyOptionsScreen(Screen screen, GameOptions gameOptions) {
        super(screen, gameOptions, new TranslatableText("My Options"));
    }

    protected void init() {
        this.list = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
        this.list.setRenderBackground(false);
        this.list.setRenderHorizontalShadows(false);
        this.list.addAll(OPTIONS);
        this.addSelectableChild(this.list);
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, ScreenTexts.DONE, (button) -> {
            MyGameOptions.write();
            //this.client.options.write();
            this.client.setScreen(this.parent);
        }));
        super.init();
    }

    public void removed() {
        MyGameOptions.write();
        this.client.options.write();
        super.removed();
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.list.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 5, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
        List<OrderedText> list = getHoveredButtonTooltip(this.list, mouseX, mouseY);
        if (list != null) {
            this.renderOrderedTooltip(matrices, list, mouseX, mouseY);
        }
    }


    static {
        OPTIONS = new Option[] {MyOptions.ENHANCED_MOVEMENT, MyOptions.FOG, MyOptions.BRIGHTNESS, MyOptions.ZOOM_AMOUNT};
    }

    public abstract static class MyOptions {
        public static final CyclingOption<Boolean> ENHANCED_MOVEMENT;
        public static final CyclingOption<Boolean> FOG;
        public static final DoubleOption BRIGHTNESS;
        public static final DoubleOption ZOOM_AMOUNT;


        static {
            ENHANCED_MOVEMENT = CyclingOption.create("options.enhancedMovement", (gameOptions) -> MyGameOptions.enhancedMovement, (gameOptions, option, enableEnhancedMovement) -> MyGameOptions.enhancedMovement = enableEnhancedMovement);
            FOG = CyclingOption.create("options.fog", (gameOptions) -> MyGameOptions.fog, (gameOptions, option, enableFog) -> MyGameOptions.fog = enableFog);
            BRIGHTNESS = new DoubleOption("options.brightness", 0.0D, 10.0D, 0.1F, (gameOptions) -> gameOptions.gamma, (gameOptions, gamma) -> gameOptions.gamma = gamma, (gameOptions, option) -> new TranslatableText("options.gamma").append(": " + Math.round(option.get(gameOptions) * 100) + "%"));
            ZOOM_AMOUNT = new DoubleOption("options.zoomAmount", 1.0D, 10.0D, 0.1F, (gameOptions) -> MyGameOptions.zoomAmount, (gameOptions, zoomAmount) -> MyGameOptions.zoomAmount = zoomAmount, (gameOptions, option) -> new TranslatableText("options.zoomAmount").append(": " + Math.round(option.get(gameOptions) * 100) + "%"));
        }
    }
}
