package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.lang.reflect.Field;

@Unique
public class BlankRenderer extends ObjectComponent {

    private Color color = Color.BLACK;

    public BlankRenderer(GameObject object) {
        super(object);
    }

    @Override
    public void onRender(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldTransform = g2d.getTransform();
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(getObject().getRotation()),
                (getObject().getCenterX() - getCamera().getRelativeX(getObject())) * getCamera().getZoom(),
                (getObject().getCenterY() - getCamera().getRelativeY(getObject())) * getCamera().getZoom());
        g2d.transform(transform);
        g2d.setColor(getColor());
        g2d.fillRect((int) ((getObject().getX() - getCamera().getRelativeX(getObject())) * getCamera().getZoom()),
                (int) ((getObject().getY() - getCamera().getRelativeY(getObject())) * getCamera().getZoom()),
                (int) ((getObject().getScaleX()) * getCamera().getZoom()),
                (int) ((getObject().getScaleY()) * getCamera().getZoom()));
        g2d.setTransform(oldTransform);
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
        } catch(Exception e) {
            c = Color.BLACK;
        }
        setColor(c);
    }

    public void setRGBColor(String rgb) {
        int r, g, b;
        try {
            String[] split = rgb.split(";");
            r = Integer.parseInt(split[0]);
            g = Integer.parseInt(split[1]);
            b = Integer.parseInt(split[2]);
        } catch(NumberFormatException | IndexOutOfBoundsException e) {
            setColor(Color.BLACK);
            return;
        }
        setColor(new Color(r, g, b));
    }

    public void setRGBAColor(String rgba) {
        int r, g, b, a;
        try {
            String[] split = rgba.split(";");
            r = Integer.parseInt(split[0]);
            g = Integer.parseInt(split[1]);
            b = Integer.parseInt(split[2]);
            a = Integer.parseInt(split[3]);
        } catch(NumberFormatException | IndexOutOfBoundsException e) {
            setColor(Color.BLACK);
            return;
        }
        setColor(new Color(r, g, b, a));
    }

}
