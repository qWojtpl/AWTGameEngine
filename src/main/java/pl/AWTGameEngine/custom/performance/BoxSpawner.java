package pl.AWTGameEngine.custom.performance;

import pl.AWTGameEngine.annotations.components.types.ComponentFX;
import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.components.Box3D;
import pl.AWTGameEngine.components.RigidBody;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.components.CameraFollow;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.TransformSet;

@ComponentGL
@ComponentFX
public class BoxSpawner extends ObjectComponent {

    private int boxCounter = 0;
    private GameObject lastObject;

    public BoxSpawner(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        spawnBoxes(1);
    }

    private void spawnBoxes(int count) {
        int size = 10;
//        for(int i = 0; i < boxCounter; i++) {
//            GameObject object = getScene().getGameObjectByName("boxspawner-" + i);
//            RigidBody.Dynamic rigidBody = (RigidBody.Dynamic) object.getComponentByClass(RigidBody.Dynamic.class);
//            rigidBody.addForce(new TransformSet(0, 10, 0));
//        }
        for(int i = 0; i < count; i++) {
            GameObject object = getScene().createGameObject("boxspawner-" + boxCounter++);
            object.setSize(new TransformSet(size, size, size));
            object.setPosition(new TransformSet(0, 100 + (size * 2 + 4) * i, 0));
            RigidBody.Dynamic rigidBody = new RigidBody.Dynamic(object);
            Box3D box3D = new Box3D(object);
            box3D.setSpriteSource("sprites/beaver.jpg");
            object.addComponent(box3D);
            object.addComponent(rigidBody);
        }
        if(lastObject != null) {
            lastObject.removeComponent(lastObject.getComponentByClass(CameraFollow.class));
        }
        lastObject = getScene().getGameObjectByName("boxspawner-" + (boxCounter - 1));
        lastObject.addComponent(new CameraFollow(lastObject));
    }

    @Override
    public void onPhysicsUpdate() {
        if(getKeyListener().hasPressedKey(69)) {
            spawnBoxes(100);
        }
    }

}
