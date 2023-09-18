package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

public abstract class ObjectComponent {

    public void onRender(GameObject object, Graphics g) {

    }

    public boolean onUpdatePosition(GameObject object, int newX, int newY) {
        return true;
    }

    public void onAddComponent(GameObject object) {

    }

}
