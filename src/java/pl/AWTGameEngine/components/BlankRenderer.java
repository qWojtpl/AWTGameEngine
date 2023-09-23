package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.lang.reflect.Field;

public class BlankRenderer extends ObjectComponent {

    private Color color = Color.BLACK;

    public BlankRenderer(GameObject object) {
        super(object);
        setUnique(true);
    }

    @Override
    public void onRender(Graphics g) {
        g.setColor(getColor());
        g.fillRect(object.getX() - Camera.getRelativeX(object), object.getY() - Camera.getRelativeY(object),
                object.getScaleX(), object.getScaleY());
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setColor(String color) {
        Color c;
        try {
            Field field = Class.forName("java.awt.Color").getField(color.toLowerCase());
            c = (Color) field.get(null);
        } catch (Exception e) {
            c = Color.BLACK;
        }
        setColor(c);
    }



}
