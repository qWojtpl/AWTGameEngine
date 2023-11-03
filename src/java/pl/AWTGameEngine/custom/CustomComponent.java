package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.components.PhysicsBody;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Vector;

public class CustomComponent extends ObjectComponent {

    private int r = 0;

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
    public void onStaticUpdate() {
        PhysicsBody body = (PhysicsBody) getObject().getComponentsByClass(PhysicsBody.class).get(0);
        if(getKeyListener().hasPressedKey(102)) {
            body.push(10, new Vector(1, 0));
        }
        if(getKeyListener().hasPressedKey(104)) {
            body.push(10, new Vector(0, -1));
        }
        if(getKeyListener().hasPressedKey(98)) {
            body.push(10, new Vector(0, 1));
        }
        if(getKeyListener().hasPressedKey(100)) {
            body.push(10, new Vector(-1, 0));
        }
    }

    @Override
    public void onButtonClick() {
        System.out.println("Clicked button!");
    }

}
