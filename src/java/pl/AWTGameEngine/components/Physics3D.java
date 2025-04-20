package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Component3D;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.TransformSet;

@Component3D
public class Physics3D extends ObjectComponent {

    private int mass = 10;
    private TransformSet velocity;

    public Physics3D(GameObject object) {
        super(object);
    }

    @Override
    public void onUpdate() {
        if(velocity.getX() > 0) {
            getObject().setX(getObject().getX() + 1);
            velocity.setX(velocity.getX() - 1);
        }

    }

}
