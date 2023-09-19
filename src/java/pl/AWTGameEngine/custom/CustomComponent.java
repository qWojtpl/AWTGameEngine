package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.engine.KeyListener;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.scenes.SceneLoader;

public class CustomComponent extends ObjectComponent {

    public CustomComponent(GameObject object) {
        super(object);
    }

    @Override
    public void onPreUpdate() {
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
        if(KeyListener.hasPressedKey(37)) {
            Camera.setX(Camera.getX() - 8);
        }
        if(KeyListener.hasPressedKey(39)) {
            Camera.setX(Camera.getX() + 8);
        }
        if(KeyListener.hasPressedKey(38)) {
            Camera.setY(Camera.getY() - 8);
        }
        if(KeyListener.hasPressedKey(40)) {
            Camera.setY(Camera.getY() + 8);
        }
        if(KeyListener.hasPressedKey(82)) {
            SceneLoader.loadScene("main");
            KeyListener.releaseKey(82);
        }
    }

}
