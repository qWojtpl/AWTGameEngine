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
        if(getKeyListener().hasPressedKey(37)) {
            getCamera().setX(getCamera().getX() - 8);
        }
        if(getKeyListener().hasPressedKey(39)) {
            getCamera().setX(getCamera().getX() + 8);
        }
        if(getKeyListener().hasPressedKey(38)) {
            getCamera().setY(getCamera().getY() - 8);
        }
        if(getKeyListener().hasPressedKey(40)) {
            getCamera().setY(getCamera().getY() + 8);
        }
        if(getKeyListener().hasPressedKey(75)) {
            getCamera().setZoom(getCamera().getZoom() - 0.25f);
            getKeyListener().releaseKey(75);
        }
        if(getKeyListener().hasPressedKey(76)) {
            getCamera().setZoom(getCamera().getZoom() + 0.25f);
            getKeyListener().releaseKey(76);
        }
        if(getKeyListener().hasPressedKey(82)) {
            getSceneLoader().loadSceneFile("main");
            getKeyListener().releaseKey(82);
        }
    }

    @Override
    public void onButtonClick() {
        System.out.println("Clicked button!");
    }

}
