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
        TransformSet vector = new TransformSet();
        if(getKeyListener().hasPressedKey(87)) { // W
            vector = new TransformSet(0, -1, 0);
        }
        if(getKeyListener().hasPressedKey(65)) { // A
            vector = new TransformSet(-1, 0, 0);
        }
        if(getKeyListener().hasPressedKey(83)) { // S
            vector = new TransformSet(0, 1, 0);
        }
        if(getKeyListener().hasPressedKey(68)) { // D
            vector = new TransformSet(1, 0, 0);
        }
        rigidBody.addForce(vector, 1);
    }

}
