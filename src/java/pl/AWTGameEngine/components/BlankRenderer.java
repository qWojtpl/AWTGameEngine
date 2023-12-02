package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.SerializationMethod;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.awt.geom.AffineTransform;

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
                    getCamera().parseX(getObject(), getObject().getCenterX()),
                    getCamera().parseY(getObject(), getObject().getCenterY()));
            g2d.transform(transform);
        }
        g2d.setColor(color.getColor());
        g2d.fillRect(
                getCamera().parseX(getObject(), getObject().getX()),
                getCamera().parseY(getObject(), getObject().getY()),
                getCamera().parseScale(getObject().getScaleX()),
                getCamera().parseScale(getObject().getScaleY())
        );
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

    @SerializationMethod
    public void setColor(String color) {
        this.color.setColor(color);
    }

}
