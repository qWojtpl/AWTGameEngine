package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.engine.GraphicsManager;
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
    public void onAfterRender(GraphicsManager g) {
        if(!isEnabled()) {
            return;
        }
        Graphics2D g2d = (Graphics2D) g.getGraphics();
        AffineTransform oldTransform = g2d.getTransform();
        if(getObject().getRotation() != 0) {
            AffineTransform transform = new AffineTransform();
            transform.rotate(Math.toRadians(getObject().getRotation()),
                    getCamera().parseX(getObject(), getObject().getCenterX()),
                    getCamera().parseY(getObject(), getObject().getCenterY()));
            g2d.transform(transform);
        }
        g2d.setColor(color.getColor());
        g.drawRect(
                getCamera().parseX(getObject(), getObject().getX()),
                getCamera().parseY(getObject(), getObject().getY()),
                getCamera().parseScale(getObject().getSizeX()),
                getCamera().parseScale(getObject().getSizeY())
        );
        g2d.setTransform(oldTransform);
    }

    @SerializationGetter
    public boolean isEnabled() {
        return this.enabled;
    }

    public ColorObject getColorObject() {
        return this.color;
    }

    @SerializationGetter
    public String getColor() {
        return this.color.serialize();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @SerializationSetter
    public void setEnabled(String enabled) {
        setEnabled(Boolean.parseBoolean(enabled));
    }

    public void setColor(ColorObject color) {
        if(color == null) {
            return;
        }
        this.color = color;
    }

    @SerializationSetter
    public void setColor(String color) {
        setColor(new ColorObject(color));
    }

}
