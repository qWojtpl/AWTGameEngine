package pl.AWTGameEngine.components;

import pl.AWTGameEngine.engine.ColliderRegistry;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

public class BoxCollider extends ObjectComponent {

    private int x1;
    private int x2;
    private int y1;
    private int y2;
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
            g.drawRect(object.getX() + x1 - Camera.getRelativeX(object), object.getY() + y1 - Camera.getRelativeY(object),
                    object.getScaleX() + x2 - x1, object.getScaleY() + y1 - y2);
        }
    }

    @Override
    public boolean onUpdatePosition(int newX, int newY) {
        return !ColliderRegistry.isColliding(object, this, newX, newY);
    }

    public int getX1() {
        return this.x1;
    }

    public int getX2() {
        return this.x2;
    }

    public int getY1() {
        return this.y1;
    }

    public int getY2() {
        return this.y2;
    }

    public boolean isVisualize() {
        return this.visualize;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public void setX1(String x1) {
        setX1(Integer.parseInt(x1));
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public void setX2(String x2) {
        setX2(Integer.parseInt(x2));
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public void setY1(String y1) {
        setY1(Integer.parseInt(y1));
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public void setY2(String y2) {
        setY2(Integer.parseInt(y2));
    }

    public void setVisualize(boolean visualize) {
        this.visualize = visualize;
    }

    public void setVisualize(String visualize) {
        setVisualize(Boolean.parseBoolean(visualize));
    }

}
