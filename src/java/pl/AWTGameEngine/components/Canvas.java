package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;

public class Canvas extends ObjectComponent {

    public Canvas(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        object.setPriority(100);
    }

    @Override
    public void onAfterUpdate() {
        object.setX(Camera.getX());
        object.setY(Camera.getY());
    }

}
