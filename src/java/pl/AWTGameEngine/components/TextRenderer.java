package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

public class TextRenderer extends ObjectComponent {

    private String text = "Empty text renderer";
    private Color color = Color.BLACK;
    private float size = 30.0f;

    public TextRenderer(GameObject object) {
        super(object);
    }

    @Override
    public void onRender(Graphics g) {
        g.setColor(getColor());
        g.setFont(g.getFont().deriveFont(getSize()));
        g.drawString(getText(), object.getX() - Camera.getX(), object.getY() - Camera.getY());
    }

    public String getText() {
        return this.text;
    }

    public Color getColor() {
        return this.color;
    }

    public float getSize() {
        return this.size;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setColor(String color) {
        setColor(Color.getColor(color));
    }

    public void setSize(float size) {
        this.size = size;
    }

    public void setSize(String size) {
        setSize(Float.parseFloat(size));
    }

}
