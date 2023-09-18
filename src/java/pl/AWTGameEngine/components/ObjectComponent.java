package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

/**
 * Every field should have dedicated method to set the value of this field.
 * This is used for deserialization.
 * Name schema: setFieldname(String)
 */
public abstract class ObjectComponent {

    public void onRender(GameObject object, Graphics g) {

    }

    public boolean onUpdatePosition(GameObject object, int newX, int newY) {
        return true;
    }

    public void onAddComponent(GameObject object) {

    }

}
