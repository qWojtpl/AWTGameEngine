package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;

public class TextRenderer extends ObjectComponent {

    private String text = "Text";
    private Color color = Color.BLACK;
    private float size = 30.0f;
    private int x = 0;
    private int y = 0;
    private Horizontal horizontal;
    private Vertical vertical;

    public TextRenderer(GameObject object) {
        super(object);
    }

    @Override
    public void onRender(Graphics g) {
        int width = g.getFontMetrics().stringWidth(text);
        if(Horizontal.LEFT.equals(horizontal)) {
            x = 0;
        } else if(Horizontal.RIGHT.equals(horizontal)) {
            x = (int) (getObject().getScaleX() - width / getCamera().getZoom());
        } else {
            x = (int) (getObject().getScaleX() / 2 - width / 2 / getCamera().getZoom());
        }
        if(Vertical.TOP.equals(vertical)) {
            y = (int) size;
        } else if(Vertical.BOTTOM.equals(vertical)) {
            y = getObject().getScaleY();
        } else {
            y = (int) ((size + getObject().getScaleY()) / 2 - size / 8);
        }
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldTransform = g2d.getTransform();
        if(getObject().getRotation() != 0) {
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
                (int) ((getObject().getY() - getCamera().getRelativeY(getObject()) + getY()) * getCamera().getZoom()));
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

    public void align(Horizontal horizontal, Vertical vertical) {
        align(horizontal);
        align(vertical);
    }

    public void align(Horizontal horizontal) {
        this.horizontal = horizontal;
    }

    public void align(Vertical vertical) {
        this.vertical = vertical;
    }

    public enum Horizontal {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum Vertical {
        TOP,
        CENTER,
        BOTTOM
    }

}
