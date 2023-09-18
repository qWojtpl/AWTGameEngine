package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.engine.KeyListener;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.scenes.SceneLoader;

public class CustomComponent extends ObjectComponent {

    public CustomComponent(GameObject object) {
        super(object);
    }

    @Override
    public void onUpdate() {
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
        if(KeyListener.hasPressedKey(82)) {
            SceneLoader.loadScene("main");
            KeyListener.releaseKey(82);
        }
    }

}
