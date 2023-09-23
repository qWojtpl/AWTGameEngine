package pl.AWTGameEngine.components;

import pl.AWTGameEngine.engine.ColliderRegistry;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

public class BoxCollider extends ObjectComponent {

    private int x;
    private int y;
    private int scaleX;
    private int scaleY;
    private boolean visualize;

    public BoxCollider(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        ColliderRegistry.registerCollider(this, object);
    }

    @Override
    public void onRender(Graphics g) {
        if(isVisualize()) {
            g.setColor(Color.GREEN);
            g.drawRect(object.getX() + x - Camera.getRelativeX(object), object.getY() + y - Camera.getRelativeY(object),
                    object.getScaleX() + scaleX, object.getScaleY() + scaleY);
        }
    }

    @Override
    public boolean onUpdatePosition(int newX, int newY) {
        return !ColliderRegistry.isColliding(object, this, newX, newY);
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

}
