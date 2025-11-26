package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.FromXML;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.awt.geom.Path2D;

public abstract class Collider extends ObjectComponent {

    protected double x = 0;
    protected double y = 0;
    protected double sizeX = 0;
    protected double sizeY = 0;
    protected boolean visualize = false;
    protected ColorObject visualizeColor = new ColorObject(Color.GREEN);

    public Collider(GameObject object) {
        super(object);
    }

    public Path2D calculatePath(double newX, double newY) {
        return new Path2D.Double();
    }

    @SerializationGetter
    public double getX() {
        return this.x;
    }

    @SerializationGetter
    public double getY() {
        return this.y;
    }

    @SerializationGetter
    public double getSizeX() {
        return this.sizeX;
    }

    @SerializationGetter
    public double getSizeY() {
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

    public void setX(double x) {
        this.x = x;
    }

    @FromXML
    public void setX(String x) {
        setX(Integer.parseInt(x));
    }

    public void setY(double y) {
        this.y = y;
    }

    @FromXML
    public void setY(String y) {
        setY(Integer.parseInt(y));
    }

    public void setSizeX(double sizeX) {
        this.sizeX = sizeX;
    }

    @FromXML
    public void setSizeX(String sizeX) {
        setSizeX(Double.parseDouble(sizeX));
    }

    public void setSizeY(double sizeY) {
        this.sizeY = sizeY;
    }

    @FromXML
    public void setSizeY(String sizeY) {
        setSizeY(Double.parseDouble(sizeY));
    }

    public void setVisualize(boolean visualize) {
        this.visualize = visualize;
    }

    @FromXML
    public void setVisualize(String visualize) {
        setVisualize(Boolean.parseBoolean(visualize));
    }

    public void setVisualizeColor(ColorObject color) {
        if(color == null) {
            return;
        }
        this.visualizeColor = color;
    }

    @FromXML
    public void setVisualizeColor(String color) {
        setVisualizeColor(new ColorObject(color));
    }

}
