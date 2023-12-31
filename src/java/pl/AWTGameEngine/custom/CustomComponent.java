package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;

public class CustomComponent extends ObjectComponent {

    public CustomComponent(GameObject object) {
        super(object);
    }

    @Override
    public void onPreUpdate() {
        if(getKeyListener().hasPressedKey(87)) {
            getObject().setY(getObject().getY() - 3);
        }
        if(getKeyListener().hasPressedKey(83)) {
            getObject().setY(getObject().getY() + 3);
        }
        if(getKeyListener().hasPressedKey(65)) {
            getObject().setX(getObject().getX() - 3);
        }
        if(getKeyListener().hasPressedKey(68)) {
            getObject().setX(getObject().getX() + 3);
        }
    }

    @Override
    public void onStaticUpdate() {
        onPreUpdate();
        if(getKeyListener().hasPressedKey(75)) {
            getCamera().setZoom(getCamera().getZoom() - 0.25f);
            getKeyListener().releaseKey(75);
        }
        if(getKeyListener().hasPressedKey(76)) {
            getCamera().setZoom(getCamera().getZoom() + 0.25f);
            getKeyListener().releaseKey(76);
        }
        if(getKeyListener().hasPressedKey(82)) {
            getSceneLoader().loadSceneFile("scenes/main.scene");
            getKeyListener().releaseKey(82);
        }
        getObject().setRotation(getObject().getRotation() + 1);
    }

    @Override
    public void onButtonClick() {
        System.out.println("Clicked button!");
    }

}
