package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.lang.reflect.Field;

@Unique
public class BlankRenderer extends ObjectComponent {

    private ColorObject color = new ColorObject();

    public BlankRenderer(GameObject object) {
        super(object);
    }

    @Override
    public void onRender(Graphics g) {
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
        g2d.fillRect((int) ((getObject().getX() - getCamera().getRelativeX(getObject())) * getCamera().getZoom()),
                (int) ((getObject().getY() - getCamera().getRelativeY(getObject())) * getCamera().getZoom()),
                (int) ((getObject().getScaleX()) * getCamera().getZoom()),
                (int) ((getObject().getScaleY()) * getCamera().getZoom()));
        g2d.setTransform(oldTransform);
    }

    public ColorObject getColor() {
        return this.color;
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

}
