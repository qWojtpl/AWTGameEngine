package pl.AWTGameEngine.objects;

import java.awt.*;
import java.lang.reflect.Field;

public class ColorObject {

    private Color color;

    public ColorObject() {
        setColor(Color.BLACK);
    }

    public ColorObject(Color color) {
        setColor(color);
    }

    public ColorObject(String color) {
        setColor(color);
    }

    public static Color deserialize(String color) {
        Color c = Color.BLACK;
        if(color.toLowerCase().startsWith("rgb")) {
            color = color.replace("(", "").replace(")", "");
            int argumentCount = 3;
            if(color.toLowerCase().startsWith("rgba")) {
                argumentCount++;
            }
            if(argumentCount == 3) {
                color = color.replace("rgb", "");
            } else {
                color = color.replace("rgba", "");
            }
            String[] split = color.split(",");
            if(split.length != argumentCount) {
                return c;
            }
            try {
                int[] args = new int[]{0, 0, 0, 255};
                for(int i = 0; i < argumentCount; i++) {
                    args[i] = Integer.parseInt(split[i]);
                }
                c = new Color(args[0], args[1], args[2], args[3]);
            } catch(NumberFormatException ignored) {}
            return c;
        }
        try {
            Field field = Class.forName("java.awt.Color").getField(color.toLowerCase());
            c = (Color) field.get(null);
        } catch(Exception ignored) {}
        return c;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setColor(String color) {
        this.color = deserialize(color);
    }

}
