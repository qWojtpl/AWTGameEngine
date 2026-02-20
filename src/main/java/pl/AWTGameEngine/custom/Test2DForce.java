package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.components.RigidBody;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.TransformSet;

@DefaultComponent
@ComponentGL
public class Test2DForce extends ObjectComponent {

    private RigidBody.Dynamic rigidBody;

    public Test2DForce(GameObject object) {
        super(object);
    }

    @Override
    public void onSerializationFinish() {
        rigidBody = (RigidBody.Dynamic) getObject().getComponentByClass(RigidBody.Dynamic.class);
    }

    @Override
    public void onPhysicsUpdate() {
        if(getKeyListener().hasPressedKey(80)) {
            rigidBody.addForce(new TransformSet(1, 0, 0), 1);
        }
//        getObject().setX(getObject().getX() + 2);
    }

}
