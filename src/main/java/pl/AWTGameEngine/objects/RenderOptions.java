package pl.AWTGameEngine.objects;

import pl.AWTGameEngine.Dependencies;

import java.awt.*;

public class RenderOptions {

    private Color color = Color.BLACK;
    private Font font = Dependencies.getWindowsManager().getDefaultFont();
    private double rotation = 0;
    private double rotationCenterX = 0;
    private double rotationCenterY = 0;
    private float stroke = 1;
    private GameObject context;

    public Color getColor() {
        return this.color;
    }

    public Font getFont() {
        return this.font;
    }

    public double getRotation() {
        return this.rotation;
    }

    public double getRotationCenterX() {
        return this.rotationCenterX;
    }

    public double getRotationCenterY() {
        return this.rotationCenterY;
    }

    public float getStroke() {
        return this.stroke;
    }

    public GameObject getContext() {
        return this.context;
    }

    public RenderOptions setColor(Color color) {
        this.color = color;
        return this;
    }

    public RenderOptions setColor(ColorObject color) {
        return setColor(color.getColor());
    }

    public RenderOptions setFont(Font font) {
        this.font = font;
        return this;
    }

    public RenderOptions setRotation(double rotation) {
        this.rotation = rotation;
        return this;
    }

    public RenderOptions setRotationCenterX(double x) {
        this.rotationCenterX = x;
        return this;
    }

    public RenderOptions setRotationCenterY(double y) {
        this.rotationCenterY = y;
        return this;
    }

    public RenderOptions setStroke(float stroke) {
        this.stroke = stroke;
        return this;
    }

    public RenderOptions setContext(GameObject context) {
        this.context = context;
        return this;
    }

}