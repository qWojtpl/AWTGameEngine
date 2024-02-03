package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;

public class CustomComponent extends ObjectComponent {

    public CustomComponent(GameObject object) {
        super(object);
    }

    @Override
    public void onPreUpdate() {
        onStaticUpdate();
    }

    @Override
    public void onStaticUpdate() {
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
            //getScene().saveSceneState("./savedscene.scene");
            getKeyListener().releaseKey(82);
        }
        if(getKeyListener().hasPressedKey(87)) {
            getObject().moveY(getObject().getY() - 3);
        }
        if(getKeyListener().hasPressedKey(83)) {
            getObject().moveY(getObject().getY() + 3);
        }
        if(getKeyListener().hasPressedKey(65)) {
            getObject().moveX(getObject().getX() - 3);
        }
        if(getKeyListener().hasPressedKey(68)) {
            getObject().moveX(getObject().getX() + 3);
        }
        getObject().rotate(getObject().getRotation() + 1);
    }

    @Override
    public void onButtonClick() {
        System.out.println("Clicked button!");
    }

}
