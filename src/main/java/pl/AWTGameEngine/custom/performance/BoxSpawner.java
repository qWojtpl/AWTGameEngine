package pl.AWTGameEngine.custom.performance;

import pl.AWTGameEngine.annotations.ComponentFX;
import pl.AWTGameEngine.annotations.ComponentGL;
import pl.AWTGameEngine.components.Box3D;
import pl.AWTGameEngine.components.RigidBody;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.TransformSet;

@ComponentGL
@ComponentFX
public class BoxSpawner extends ObjectComponent {

    private boolean counterEnabled = true;
    private double updateFPS = 0;
    private double renderFPS = 0;
    private double physicsFPS = 0;
    private int boxCounter = 0;

    public BoxSpawner(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        new Thread(() -> {
            while(counterEnabled) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                printFPS();
            }
        }).start();
        spawnBoxes(1500);
    }

    @Override
    public void onRemoveComponent() {
        counterEnabled = false;
    }

    private void printFPS() {
        System.out.println("Update FPS: " + updateFPS);
        System.out.println("Render FPS: " + renderFPS);
        System.out.println("Physics FPS: " + physicsFPS);
        if(updateFPS > 1) {
            System.out.println("Cubes: " + (getScene().getGameObjects().size() - 3)); // player, floor, textures
        }
        updateFPS = 0;
        renderFPS = 0;
        physicsFPS = 0;
    }

    private void spawnBoxes(int count) {
        int size = 10;
        for(int i = 0; i < boxCounter; i++) {
            GameObject object = getScene().getGameObjectByName("boxspawner-" + i);
            RigidBody.Dynamic rigidBody = (RigidBody.Dynamic) object.getComponentByClass(RigidBody.Dynamic.class);
            rigidBody.addForce(new TransformSet(0, 10, 0));
        }
        for(int i = 0; i < count; i++) {
            GameObject object = getScene().createGameObject("boxspawner-" + boxCounter++);
            object.setSize(new TransformSet(size, size, size));
            object.setPosition(new TransformSet(0, 100 + (size + 4) * i, 0));
            RigidBody.Dynamic rigidBody = new RigidBody.Dynamic(object);
            Box3D box3D = new Box3D(object);
            box3D.setGlTexture("beaver");
            object.addComponent(box3D);
            object.addComponent(rigidBody);
        }
    }

    @Override
    public void onUpdate() {
        updateFPS++;
    }

    @Override
    public void onPhysicsUpdate() {
        physicsFPS++;
        if(getKeyListener().hasPressedKey(69)) {
            spawnBoxes(500);
        }
    }

    @Override
    public void on3DRenderRequest(GraphicsManager3D g) {
        renderFPS++;
    }


}
