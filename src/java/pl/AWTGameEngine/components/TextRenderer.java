package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.lang.reflect.Field;

public class TextRenderer extends ObjectComponent {

    private String text = "Text";
    private Color color = Color.BLACK;
    private float size = 30.0f;
    private int x = 0;
    private int y = 0;

    public TextRenderer(GameObject object) {
        super(object);
    }

    @Override
    public void onRender(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldTransform = g2d.getTransform();
        if(getObject().getRotation() > 0) {
            AffineTransform transform = new AffineTransform();
            transform.rotate(Math.toRadians(getObject().getRotation()),
                    (getObject().getCenterX() - getCamera().getRelativeX(getObject())) * getCamera().getZoom(),
                    (getObject().getCenterY() - getCamera().getRelativeY(getObject())) * getCamera().getZoom());
            g2d.transform(transform);
        }
        g2d.setColor(getColor());
        g2d.setFont(g.getFont().deriveFont(getSize() * getCamera().getZoom()));
        g2d.drawString(getText(),
                (int) ((getObject().getX() - getCamera().getRelativeX(getObject()) + getX()) * getCamera().getZoom()),
                (int) ((getObject().getY() - getCamera().getRelativeY(getObject()) + getY() + getObject().getScaleY() - getSize() / 2.5)
                        * getCamera().getZoom()));
        g2d.setTransform(oldTransform);
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

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setText(String text) {
        this.text = text;
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

    public void setSize(float size) {
        this.size = size;
    }

    public void setSize(String size) {
        setSize(Float.parseFloat(size));
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setX(String x) {
        try {
            setX(Integer.parseInt(x));
        } catch(NumberFormatException e) {
            setX(0);
        }
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setY(String y) {
        try {
            setY(Integer.parseInt(y));
        } catch(NumberFormatException e) {
            setY(0);
        }
    }

}
