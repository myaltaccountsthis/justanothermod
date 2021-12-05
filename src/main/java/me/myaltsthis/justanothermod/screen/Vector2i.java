package me.myaltsthis.justanothermod.screen;

public class Vector2i {
    public final int x;
    public final int y;

    public Vector2i() {
        x = 0;
        y = 0;
    }
    public Vector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static String encode(Vector2i vector) {
        return "x:" + vector.x + ",y:" + vector.y;
    }
    public static Vector2i decode(String str) {
        try {
            int xIndex = str.indexOf("x:");
            int yIndex = str.indexOf("y:");
            int comma  = str.indexOf(",");
            return new Vector2i(Integer.parseInt(str.substring(xIndex + 2, comma)), Integer.parseInt(str.substring(yIndex + 2)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
