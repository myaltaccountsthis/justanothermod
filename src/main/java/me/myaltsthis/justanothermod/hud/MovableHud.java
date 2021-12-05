package me.myaltsthis.justanothermod.hud;

import me.myaltsthis.justanothermod.screen.Vector2i;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public class MovableHud extends DrawableHelper {
    protected Vector2i position;
    protected Vector2i size;

    public MovableHud(Vector2i pos, Vector2i size) {
        this.position = pos;
        this.size = size;
    }
    public MovableHud(int xPos, int yPos, int xSize, int ySize) {
        this(new Vector2i(xPos, yPos), new Vector2i(xSize, ySize));
    }

    public void render(MatrixStack matrices) {

    }
}
