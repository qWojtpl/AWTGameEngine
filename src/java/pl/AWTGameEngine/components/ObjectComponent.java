package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.GameObject;

public abstract class ObjectComponent {

    public void onRender(GameObject object) {

    }

    public boolean onUpdatePosition(GameObject object, int newX, int newY) {
        return true;
    }

    public void onAddComponent(GameObject object) {

    }

}
