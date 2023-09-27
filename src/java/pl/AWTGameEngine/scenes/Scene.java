package pl.AWTGameEngine.scenes;

import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.engine.ColliderRegistry;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import java.util.Collection;
import java.util.HashMap;

public class Scene {

    private final String name;
    private final HashMap<String, GameObject> gameObjects = new HashMap<>();
    private final Window window;
    private ColliderRegistry colliderRegistry;
    private Camera camera;

    public Scene(String name, Window window) {
        this.name = name;
        this.window = window;
        this.colliderRegistry = new ColliderRegistry();
        this.camera = new Camera();
    }

    public String getName() {
        return this.name;
    }

    public Window getWindow() {
        return this.window;
    }

    public ColliderRegistry getColliderRegistry() {
        return this.colliderRegistry;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public void setColliderRegistry(ColliderRegistry registry) {
        this.colliderRegistry = registry;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public GameObject createGameObject(String identifier) {
        if(gameObjects.containsKey(identifier)) {
            return createGameObject(identifier + "!");
        }
        GameObject go = new GameObject(identifier, this);
        gameObjects.put(identifier, go);
        return go;
    }

    public void addGameObject(GameObject object) {
        if(gameObjects.containsKey(object.getIdentifier())) {
            System.out.println("Object with this identifier already exists.");
            return;
        }
        if(!object.getScene().equals(this)) {
            System.out.println("Cannot add object which doesn't have this scene as a scene.");
        }
        gameObjects.put(object.getIdentifier(), object);
    }

    public Collection<GameObject> getGameObjects() {
        return gameObjects.values();
    }

    public void removeAllObjects() {
        gameObjects.clear();
    }

    public void update() {
        for(GameObject go : getGameObjects()) {
            for(ObjectComponent component : go.getComponents()) {
                component.onPreUpdate();
            }
        }
        for(GameObject go : getGameObjects()) {
            for(ObjectComponent component : go.getComponents()) {
                component.onUpdate();
            }
        }
        for(GameObject go : getGameObjects()) {
            for(ObjectComponent component : go.getComponents()) {
                component.onAfterUpdate();
            }
        }
    }

}
