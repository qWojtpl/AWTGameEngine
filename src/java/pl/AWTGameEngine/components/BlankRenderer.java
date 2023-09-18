package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

public class BlankRenderer extends ObjectComponent {

    private Color color = Color.BLACK;

    @Override
    public void onRender(GameObject object, Graphics g) {
        g.setColor(getColor());
        g.fillRect(object.getX(), object.getY(), object.getScaleX(), object.getScaleY());
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }



}
