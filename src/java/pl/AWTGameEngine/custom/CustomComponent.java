package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.engine.KeyListener;
import pl.AWTGameEngine.objects.GameObject;

public class CustomComponent extends ObjectComponent {

    @Override
    public void onUpdate(GameObject object) {
        if(KeyListener.hasPressedKey(87)) {
            object.setY(object.getY() - 3);
        }
        if(KeyListener.hasPressedKey(83)) {
            object.setY(object.getY() + 3);
        }
        if(KeyListener.hasPressedKey(65)) {
            object.setX(object.getX() - 3);
        }
        if(KeyListener.hasPressedKey(68)) {
            object.setX(object.getX() + 3);
        }
    }

}
