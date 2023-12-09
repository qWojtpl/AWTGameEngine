package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.awt.geom.Path2D;

public abstract class Collider extends ObjectComponent {

    protected int x = 0;
    protected int y = 0;
    protected int sizeX = 0;
    protected int sizeY = 0;
    protected boolean visualize = false;
    protected ColorObject visualizeColor = new ColorObject(Color.GREEN);

    public Collider(GameObject object) {
        super(object);
    }

    public Path2D calculatePath(int newX, int newY) {
        return new Path2D.Double();
    }

    @SerializationGetter
    public int getX() {
        return this.x;
    }

    @SerializationGetter
    public int getY() {
        return this.y;
    }

    @SerializationGetter
    public int getSizeX() {
        return this.sizeX;
    }

    @SerializationGetter
    public int getSizeY() {
        return this.sizeY;
    }

    public Path2D getPath() {
        return new Path2D.Double();
    }

    @SerializationGetter
    public boolean isVisualize() {
        return this.visualize;
    }

    public ColorObject getVisualizeColorObject() {
        return this.visualizeColor;
    }

    @SerializationGetter
    public String getVisualizeColor() {
        return this.visualizeColor.serialize();
    }

    public void setX(int x) {
        this.x = x;
    }

    @SerializationSetter
    public void setX(String x) {
        setX(Integer.parseInt(x));
    }

    public void setY(int y) {
        this.y = y;
    }

    @SerializationSetter
    public void setY(String y) {
        setY(Integer.parseInt(y));
    }

    public void setSizeX(int sizeX) {
        this.sizeX = sizeX;
    }

    @SerializationSetter
    public void setSizeX(String sizeX) {
        setSizeX(Integer.parseInt(sizeX));
    }

    public void setSizeY(int sizeY) {
        this.sizeY = sizeY;
    }

    @SerializationSetter
    public void setSizeY(String sizeY) {
        setSizeY(Integer.parseInt(sizeY));
    }

    public void setVisualize(boolean visualize) {
        this.visualize = visualize;
    }

    @SerializationSetter
    public void setVisualize(String visualize) {
        setVisualize(Boolean.parseBoolean(visualize));
    }

    public void setVisualizeColor(ColorObject color) {
        if(color == null) {
            return;
        }
        this.visualizeColor = color;
    }

    @SerializationSetter
    public void setVisualizeColor(String color) {
        setVisualizeColor(new ColorObject(color));
    }

}
