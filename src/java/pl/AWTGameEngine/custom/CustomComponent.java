package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.components.PhysicsBody;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Vector;

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
        PhysicsBody physicsBody = (PhysicsBody) getObject().getComponentByClass(PhysicsBody.class);
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
        if(getKeyListener().hasPressedKey(87)) {
            //getObject().moveY(getObject().getY() - 3);
            physicsBody.push(new Vector(0, -3));
        }
        if(getKeyListener().hasPressedKey(83)) {
            //getObject().moveY(getObject().getY() + 3);
            physicsBody.push(new Vector(0, 3));
        }
        if(getKeyListener().hasPressedKey(65)) {
            //getObject().moveX(getObject().getX() - 3);
            physicsBody.push(new Vector(-3, 0));
        }
        if(getKeyListener().hasPressedKey(68)) {
            //getObject().moveX(getObject().getX() + 3);
            physicsBody.push(new Vector(3, 0));
        }
        //getObject().rotate(getObject().getRotation() + 8);
    }

    @Override
    public void onButtonClick() {
        System.out.println("Clicked button!");
    }

}
