package pl.AWTGameEngine.scenes;

import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.engine.ColliderRegistry;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import java.util.*;

public class Scene {

    private final String name;
    private final LinkedHashMap<String, GameObject> gameObjects = new LinkedHashMap<>();
    private LinkedHashMap<Integer, List<GameObject>> sortedObjects = new LinkedHashMap<>();
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
        addGameObject(go);
        return go;
    }

    public void addGameObject(GameObject object) {
        if(gameObjects.containsKey(object.getIdentifier())) {
            System.out.println("Object with this identifier already exists.");
            return;
        }
        if(!this.equals(object.getScene())) {
            System.out.println("Cannot add object which doesn't have this scene as a scene.");
            return;
        }
        sortObjects();
        for(GameObject obj : getGameObjects()) {
            for(ObjectComponent component : obj.getComponents()) {
                component.onCreateGameObject(object);
            }
        }
        gameObjects.put(object.getIdentifier(), object);
    }

    public Collection<GameObject> getGameObjects() {
        return gameObjects.values();
    }

    public LinkedHashMap<Integer, List<GameObject>> getSortedObjects() {
        return this.sortedObjects;
    }

    public void sortObjects() {
        sortedObjects = new LinkedHashMap<>();
        List<Integer> priorities = new ArrayList<>();
        int maxPriority = 0;
        for(GameObject go : getGameObjects()) {
            if(go.getPriority() > maxPriority) {
                maxPriority = go.getPriority();
            }
            if(!priorities.contains(go.getPriority())) {
                priorities.add(go.getPriority());
            }
        }
        for(int i = 0; i <= maxPriority; i++) {
            if(priorities.contains(i)) {
                sortedObjects.put(i, new ArrayList<>());
            }
        }
        for(GameObject go : getGameObjects()) {
            List<GameObject> objects = sortedObjects.getOrDefault(go.getPriority(), new ArrayList<>());
            objects.add(go);
            sortedObjects.put(go.getPriority(), objects);
        }
    }

    public void removeAllObjects() {
        gameObjects.clear();
    }

    public void update() {
        if(window.isStaticMode()) {
            for(GameObject go : getGameObjects()) {
                for(ObjectComponent component : go.getComponents()) {
                    component.onStaticUpdate();
                }
            }
            window.getMouseListener().refresh();
            return;
        }
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
        window.getMouseListener().refresh();
    }

}
