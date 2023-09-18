package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

/**
 * Every field should have dedicated method to set the value of this field.
 * This is used for deserialization.
 * Name schema: setFieldname(String)
 */
public abstract class ObjectComponent {

    protected final GameObject object;

    public ObjectComponent(GameObject object) {
        this.object = object;
    }

    public void onPreUpdate() {

    }

    public void onUpdate() {

    }

    public void onAfterUpdate() {

    }

    public void onRender(Graphics g) {

    }

    public boolean onUpdatePosition(int newX, int newY) {
        return true;
    }

    public void onAddComponent() {

    }

    public void onRemoveComponent() {

    }

}
