package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.annotations.DefaultComponent;
import pl.AWTGameEngine.annotations.WebComponent;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;

@DefaultComponent
@WebComponent
public class Movement2D extends ObjectComponent {

    private final double speed = 2;

    public Movement2D(GameObject object) {
        super(object);
    }

    @Override
    public void onUpdate() {
        if(!getWindow().isFocused()) {
            return;
        }

        if(getKeyListener().hasPressedKey(87)) { // W
            getObject().setPosition(getObject().getPosition().setY(getObject().getY() - speed));
        }
        if(getKeyListener().hasPressedKey(65)) { // A
            getObject().setPosition(getObject().getPosition().setX(getObject().getX() - speed));
        }
        if(getKeyListener().hasPressedKey(83)) { // S
            getObject().setPosition(getObject().getPosition().setY(getObject().getY() + speed));
        }
        if(getKeyListener().hasPressedKey(68)) { // D
            getObject().setPosition(getObject().getPosition().setX(getObject().getX() + speed));
        }

    }

}
