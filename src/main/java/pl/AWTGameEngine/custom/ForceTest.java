package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.annotations.ComponentGL;
import pl.AWTGameEngine.components.RigidBody;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.TransformSet;

@ComponentGL
public class ForceTest extends ObjectComponent {

    public ForceTest(GameObject object) {
        super(object);
    }

    @Override
    public void onUpdate() {
        RigidBody.Dynamic rigidBody = (RigidBody.Dynamic) getScene().getGameObjectByName("box0").getComponentByClass(RigidBody.Dynamic.class);
        if(getKeyListener().hasPressedKey(87)) {
            rigidBody.addForce(new TransformSet(0, 1, 0));
        }
    }

}
