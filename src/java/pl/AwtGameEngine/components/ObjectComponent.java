package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.GameObject;

public abstract class ObjectComponent {

    public void onRender(GameObject object) {

    }

    public boolean onUpdatePosition(int newX, int newY) {
        return true;
    }

}
