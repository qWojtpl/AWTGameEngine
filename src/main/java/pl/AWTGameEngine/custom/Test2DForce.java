package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.components.RigidBody;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.TransformSet;

@DefaultComponent
public class Test2DForce extends ObjectComponent {

    private RigidBody.TopDown rigidBody;

    public Test2DForce(GameObject object) {
        super(object);
    }

    @Override
    public void onSerializationFinish() {
        rigidBody = (RigidBody.TopDown) getObject().getComponentByClass(RigidBody.TopDown.class);
    }

    @Override
    public void onPhysicsUpdate() {
        if(getKeyListener().hasPressedKey(80)) {
            rigidBody.addForce(new TransformSet(1, 1, 0), 1);
        }
    }

}
