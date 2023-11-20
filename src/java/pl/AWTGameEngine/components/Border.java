package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.RemoveOnCompile;
import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.lang.reflect.Field;

@Unique
public class Border extends ObjectComponent {

    private boolean enabled = true;
    private ColorObject color = new ColorObject();

    public Border(GameObject object) {
        super(object);
    }

    @Override
    public void onAfterRender(Graphics g) {
        if(!isEnabled()) {
            return;
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
        g2d.drawRect((int) ((getObject().getX() - getCamera().getRelativeX(getObject())) * getCamera().getZoom()),
                (int) ((getObject().getY() - getCamera().getRelativeY(getObject())) * getCamera().getZoom()),
                (int) ((getObject().getScaleX()) * getCamera().getZoom()),
                (int) ((getObject().getScaleY()) * getCamera().getZoom()));
        g2d.setTransform(oldTransform);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public ColorObject getColor() {
        return this.color;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setEnabled(String enabled) {
        setEnabled(Boolean.parseBoolean(enabled));
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
