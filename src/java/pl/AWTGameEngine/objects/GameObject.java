package pl.AWTGameEngine.objects;

import pl.AWTGameEngine.Main;
import pl.AWTGameEngine.components.ObjectComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameObject {

    private final String identifier;
    private int x = 0;
    private int y = 0;
    private int scaleX = 100;
    private int scaleY = 100;
    private List<ObjectComponent> components = new ArrayList<>();

    public GameObject(String identifier) {
        this.identifier = identifier;
    }

    public void addComponent(ObjectComponent component) {
        this.components.add(component);
        component.onAddComponent(this);
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public List<ObjectComponent> getComponents() {
        return this.components;
    }

    public List<ObjectComponent> getComponentsByClass(Class<?> clazz) {
        List<ObjectComponent> componentList = new ArrayList<>();
        for(ObjectComponent component : components) {
            if(component.getClass().equals(clazz)) {
                componentList.add(component);
            }
        }
        return componentList;
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

    public void setScaleX(int x) {
        this.scaleX = x;
    }

    public void setScaleY(int y) {
        this.scaleY = y;
    }

    public void render(Graphics g) {
        for(ObjectComponent component : getComponents()) {
            component.onRender(this, g);
        }
    }

    public boolean updatePosition(int newX, int newY) {
        boolean returnable = true;
        for(ObjectComponent component : getComponents()) {
            boolean good = component.onUpdatePosition(this, newX, newY);
            if(!good) {
                returnable = false;
            }
        }
        return returnable;
    }

}
