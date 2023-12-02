package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.SerializationMethod;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.awt.geom.AffineTransform;

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
                    getCamera().parseX(getObject(), getObject().getCenterX()),
                    getCamera().parseY(getObject(), getObject().getCenterY()));
            g2d.transform(transform);
        }
        g2d.setColor(color.getColor());
        g2d.drawRect(
                getCamera().parseX(getObject(), getObject().getX()),
                getCamera().parseY(getObject(), getObject().getY()),
                getCamera().parseScale(getObject().getScaleX()),
                getCamera().parseScale(getObject().getScaleY())
        );
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

    @SerializationMethod
    public void setEnabled(String enabled) {
        setEnabled(Boolean.parseBoolean(enabled));
    }

    public void setColor(ColorObject color) {
        if(color == null) {
            return;
        }
        this.color = color;
    }

    @SerializationMethod
    public void setColor(String color) {
        setColor(new ColorObject(color));
    }

}
