package pl.AWTGameEngine.custom.performance;

import pl.AWTGameEngine.annotations.ComponentFX;
import pl.AWTGameEngine.annotations.ComponentGL;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;

@ComponentGL
@ComponentFX
public class SpinningObject extends ObjectComponent {

    private int speed = 10;

    public SpinningObject(GameObject object) {
        super(object);
    }

    @Override
    public void onUpdate() {
        getObject().setRotation(getObject().getRotation().clone().setX(getObject().getRotation().getX() + speed));
        getObject().setPosition(getObject().getPosition().clone());
    }

}
