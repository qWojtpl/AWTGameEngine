package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.lang.reflect.Field;

public class BoxCollider extends ObjectComponent {

    private int x;
    private int y;
    private int scaleX;
    private int scaleY;
    private boolean visualize;
    private Color visualizeColor = Color.GREEN;

    public BoxCollider(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        getColliderRegistry().registerCollider(this, getObject());
    }

    @Override
    public void onRemoveComponent() {
        getColliderRegistry().removeCollider(this);
    }

    @Override
    public void onRender(Graphics g) {
        if(isVisualize()) {
            Graphics2D g2d = (Graphics2D) g;
            AffineTransform oldTransform = g2d.getTransform();
            AffineTransform transform = new AffineTransform();
            transform.rotate(Math.toRadians(getObject().getRotation()),
                    (getObject().getCenterX() - getCamera().getRelativeX(getObject())) * getCamera().getZoom(),
                    (getObject().getCenterY() - getCamera().getRelativeY(getObject())) * getCamera().getZoom());
            g2d.transform(transform);
            g2d.setColor(visualizeColor);
            g2d.drawRect((int) ((getObject().getX() + x - getCamera().getRelativeX(getObject())) * getCamera().getZoom()),
                    (int) ((getObject().getY() + y - getCamera().getRelativeY(getObject())) * getCamera().getZoom()),
                    (int) ((getObject().getScaleX() + scaleX) * getCamera().getZoom()),
                    (int) ((getObject().getScaleY() + scaleY) * getCamera().getZoom()));
            g2d.setTransform(oldTransform);
        }
    }

    @Override
    public boolean onUpdatePosition(int newX, int newY) {
        if(getColliderRegistry().isColliding(getObject(), this, newX, newY)) {
            for(ObjectComponent component : getObject().getComponents()) {
                component.onCollide();
            }
            return false;
        }
        return true;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getScaleX() {
        return this.scaleX;
    }

    public int getScaleY() {
        return this.scaleY;
    }

    public boolean isVisualize() {
        return this.visualize;
    }

    public Color getVisualizeColor() {
        return this.visualizeColor;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setX(String x) {
        setX(Integer.parseInt(x));
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setY(String y) {
        setY(Integer.parseInt(y));
    }

    public void setScaleX(int scaleX) {
        this.scaleX = scaleX;
    }

    public void setScaleX(String scaleX) {
        setScaleX(Integer.parseInt(scaleX));
    }

    public void setScaleY(int scaleY) {
        this.scaleY = scaleY;
    }

    public void setScaleY(String scaleY) {
        setScaleY(Integer.parseInt(scaleY));
    }

    public void setVisualize(boolean visualize) {
        this.visualize = visualize;
    }

    public void setVisualize(String visualize) {
        setVisualize(Boolean.parseBoolean(visualize));
    }

    public void setVisualizeColor(Color color) {
        this.visualizeColor = color;
    }

    public void setVisualizeColor(String color) {
        Color c;
        try {
            Field field = Class.forName("java.awt.Color").getField(color.toLowerCase());
            c = (Color) field.get(null);
        } catch (Exception e) {
            c = Color.BLACK;
        }
        setVisualizeColor(c);
    }

}
