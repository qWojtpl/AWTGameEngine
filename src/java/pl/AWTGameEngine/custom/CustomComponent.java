package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.components.PhysicsBody;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Vector;

public class CustomComponent extends ObjectComponent {

    private int dir = 1;

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
            getSceneLoader().loadSceneFile("scenes/editor.xml");
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
        if(getObject().getX() > 960 || getObject().getX() < 0) {
            dir *= -1;
        }
        getObject().setX(getObject().getX() + dir);
        updateCameraPosition();
    }

    private void updateCameraPosition() {
        Camera screenCamera = getCamera();
        if(getKeyListener().hasPressedKey(37)) {
            screenCamera.setX(screenCamera.getX() - 8);
        }
        if(getKeyListener().hasPressedKey(38)) {
            screenCamera.setY(screenCamera.getY() - 8);
        }
        if(getKeyListener().hasPressedKey(39)) {
            screenCamera.setX(screenCamera.getX() + 8);
        }
        if(getKeyListener().hasPressedKey(40)) {
            screenCamera.setY(screenCamera.getY() + 8);
        }
    }

    @Override
    public void onButtonClick() {
        System.out.println("Clicked button!");
    }

}
