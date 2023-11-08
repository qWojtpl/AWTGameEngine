package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.awt.geom.Path2D;
import java.lang.reflect.Field;

public abstract class Collider extends ObjectComponent {

    protected int x = 0;
    protected int y = 0;
    protected int scaleX = 0;
    protected int scaleY = 0;
    protected boolean visualize = false;
    protected Color visualizeColor = Color.GREEN;

    public Collider(GameObject object) {
        super(object);
    }

    public Path2D calculatePath(int newX, int newY) {
        return new Path2D.Double();
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

    public Path2D getPath() {
        return new Path2D.Double();
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
