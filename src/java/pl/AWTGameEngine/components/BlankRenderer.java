package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

public class BlankRenderer extends ObjectComponent {

    private Color color = Color.BLACK;

    public BlankRenderer(GameObject object) {
        super(object);
    }

    @Override
    public void onRender(Graphics g) {
        g.setColor(getColor());
        g.fillRect(object.getX() - Camera.getX(), object.getY() - Camera.getY(), object.getScaleX(), object.getScaleY());
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setColor(String color) {
        setColor(Color.getColor(color));
    }



}
