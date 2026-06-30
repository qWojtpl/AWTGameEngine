package pl.AWTGameEngine.custom.performance;

import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.components.CameraFollow;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.transform.TransformSet;

@ComponentGL
public class BoxSpawner extends ObjectComponent {

    private int boxCounter = 0;

    public BoxSpawner(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        if(boxCounter == 0) {
            spawnBoxes(1);
        }
    }

    private void spawnBoxes(int count) {
        int size = 10;
//        for(int i = 0; i < boxCounter; i++) {
//            GameObject object = getScene().getGameObjectByName("boxspawner-" + i);
//            RigidBody.Dynamic rigidBody = (RigidBody.Dynamic) object.getComponentByClass(RigidBody.Dynamic.class);
//            rigidBody.addForce(new TransformSet(0, 10, 0));
//        }
        GameObject lastObject = getScene().getGameObjectByName("boxspawner-" + (boxCounter - 1));
        for(int i = 0; i < count; i++) {
            getScene().createGameObjectFromPrefab(
                    "boxspawner-" + boxCounter++,
                    "box",
                    new TransformSet(0, 100 + (size * 2 + 4) * i, 0),
                    new TransformSet(size, size, size)
            );
        }
        if(lastObject != null) {
            lastObject.removeComponent(lastObject.getComponentByClass(CameraFollow.class));
        }
        lastObject = getScene().getGameObjectByName("boxspawner-" + (boxCounter - 1));
        lastObject.addComponent(new CameraFollow(lastObject));
    }

    @Override
    public void onUpdate() {
        if(getKeyListener().hasPressedKey(69)) {
            spawnBoxes(100);
        }
    }

    @SaveState(name = "iterator")
    public int getIterator() {
        return this.boxCounter;
    }

    @FromXML
    public void setIterator(int iterator) {
        this.boxCounter = iterator;
    }

}
