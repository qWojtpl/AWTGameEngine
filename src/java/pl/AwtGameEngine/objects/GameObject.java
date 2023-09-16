package pl.AWTGameEngine.objects;

import pl.AWTGameEngine.components.ObjectComponent;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

    private int x = 0;
    private int y = 0;
    private int scaleX = 100;
    private int scaleY = 100;
    private List<ObjectComponent> components = new ArrayList<>();

    public List<ObjectComponent> getComponents() {
        return this.components;
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

    public boolean setX(int x) {
        if(!updatePosition(x, getY())) {
            return false;
        }
        this.x = x;
        return true;
    }

    public boolean setY(int y) {
        if(!updatePosition(getX(), y)) {
            return false;
        }
        this.y = y;
        return true;
    }

    public void render() {
        for(ObjectComponent component : getComponents()) {
            component.onRender(this);
        }
    }

    public boolean updatePosition(int newX, int newY) {
        boolean returnable = true;
        for(ObjectComponent component : getComponents()) {
            boolean good = component.onUpdatePosition(newX, newY);
            if(!good) {
                returnable = false;
            }
        }
        return returnable;
    }

}
