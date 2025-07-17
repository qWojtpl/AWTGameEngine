package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.annotations.ComponentFX;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.objects.GameObject;

@ComponentFX
public class CustomComponent extends ObjectComponent {

    private final int value = 10;

    public CustomComponent(GameObject object) {
        super(object);
    }

    @Override
    public void onUpdate() {
        // camera movement
        if(getKeyListener().hasPressedKey(87)) {
            getCamera().setZ(getCamera().getZ() + value);
        }
        if(getKeyListener().hasPressedKey(83)) {
            getCamera().setZ(getCamera().getZ() - value);
        }
        if(getKeyListener().hasPressedKey(65)) {
            getCamera().setX(getCamera().getX() - value);
        }
        if(getKeyListener().hasPressedKey(68)) {
            getCamera().setX(getCamera().getX() + value);
        }
        if(getKeyListener().hasPressedKey(81)) {
            getCamera().setY(getCamera().getY() - value);
        }
        if(getKeyListener().hasPressedKey(90)) {
            getCamera().setY(getCamera().getY() + value);
        }
        //camera rotation
        while(getCamera().getRotation().getY() >= 360) {
            getCamera().setRotationY(getCamera().getRotation().getY() - 360);
        }
/*        if(getKeyListener().hasPressedKey(102)) {
            getCamera().setRotationY(getCamera().getRotation().getY() + value / 10);
        }*/
/*        if(getKeyListener().hasPressedKey(100)) {
            getCamera().setRotationY(getCamera().getRotation().getY() - value / 10);
        }*/
    }

    @Override
    public void onMouseClick() {
        Logger.log(1, "Clicked");
    }

}
