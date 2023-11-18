package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class TextRenderer extends ObjectComponent {

    private String text = "Text";
    private ColorObject color = new ColorObject();
    private float size = 30.0f;
    private int x = 0;
    private int y = 0;
    private HorizontalAlign horizontal;
    private VerticalAlign vertical;

    public TextRenderer(GameObject object) {
        super(object);
    }

    @Override
    public void onRender(Graphics g) {
        int width = g.getFontMetrics(getWindow().getFont().deriveFont(size)).stringWidth(text);
        if(HorizontalAlign.LEFT.equals(horizontal)) {
            x = 0;
        } else if(HorizontalAlign.RIGHT.equals(horizontal)) {
            x = getObject().getScaleX() - width;
        } else {
            x = getObject().getScaleX() / 2 - width / 2;
        }
        if(VerticalAlign.TOP.equals(vertical)) {
            y = (int) size;
        } else if(VerticalAlign.BOTTOM.equals(vertical)) {
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
        g2d.setColor(color.getColor());
        g2d.setFont(g.getFont().deriveFont(getSize() * getCamera().getZoom()));
        g2d.drawString(getText(),
                (int) ((getObject().getX() - getCamera().getRelativeX(getObject()) + getX()) * getCamera().getZoom()),
                (int) ((getObject().getY() - getCamera().getRelativeY(getObject()) + getY()) * getCamera().getZoom()));
        g2d.setTransform(oldTransform);
    }

    public String getText() {
        return this.text;
    }

    public ColorObject getColor() {
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

    public void setColor(ColorObject color) {
        if(color == null) {
            return;
        }
        this.color = color;
    }

    public void setColor(String color) {
        this.color.setColor(color);
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

    public void align(HorizontalAlign horizontal, VerticalAlign vertical) {
        align(horizontal);
        align(vertical);
    }

    public void align(HorizontalAlign horizontal) {
        this.horizontal = horizontal;
    }

    public void align(VerticalAlign vertical) {
        this.vertical = vertical;
    }

    public enum HorizontalAlign {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum VerticalAlign {
        TOP,
        CENTER,
        BOTTOM
    }

}
