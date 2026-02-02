package pl.AWTGameEngine.custom.performance;

import pl.AWTGameEngine.annotations.components.types.ComponentFX;
import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;

@ComponentGL
@ComponentFX
public class SpinningObject extends ObjectComponent {

    private double speed = 10;

    public SpinningObject(GameObject object) {
        super(object);
    }

    @Override
    public void onUpdate() {
        getObject().setRotation(getObject().getRotation().clone().setX(getObject().getRotation().getX() + speed));
        getObject().setPosition(getObject().getPosition().clone());
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @FromXML
    public void setSpeed(String speed) {
        setSpeed(Double.parseDouble(speed));
    }

}
