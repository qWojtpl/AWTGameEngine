package pl.AWTGameEngine.scenes;

import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.engine.ColliderRegistry;
import pl.AWTGameEngine.engine.PanelRegistry;
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
    private PanelRegistry panelRegistry;
    private Camera camera;

    public Scene(String name, Window window) {
        this.name = name;
        this.window = window;
        setColliderRegistry(new ColliderRegistry());
        setPanelRegistry(new PanelRegistry());
        setCamera(new Camera());
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

    public PanelRegistry getPanelRegistry() {
        return this.panelRegistry;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public void setColliderRegistry(ColliderRegistry registry) {
        this.colliderRegistry = registry;
    }

    public void setPanelRegistry(PanelRegistry registry) {
        this.panelRegistry = registry;
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
        object.setPanel(window.getPanel());
        gameObjects.put(object.getIdentifier(), object);
        addSortedObject(object.getPriority(), object);
        for(GameObject obj : getActiveGameObjects()) {
            if(obj.equals(object)) {
                continue;
            }
            for(ObjectComponent component : obj.getComponents()) {
                component.onCreateGameObject(object);
            }
        }
    }

    public void removeGameObject(GameObject object) {
        if(object == null) {
            return;
        }
        for(ObjectComponent component : object.getComponents()) {
            component.onRemoveComponent();
        }
        for(GameObject child : object.getChildren()) {
            child.setParent(object.getParent());
        }
        gameObjects.remove(object.getIdentifier());
        removeSortedObject(object.getPriority(), object);
        for(GameObject obj : getActiveGameObjects()) {
            if(obj.equals(object)) {
                continue;
            }
            for(ObjectComponent component : obj.getComponents()) {
                component.onRemoveGameObject(object);
            }
        }
    }

    public void addSortedObject(int priority, GameObject object) {
        boolean hasKey = false;
        ArrayList<GameObject> empty = new ArrayList<>();
        List<GameObject> objects = sortedObjects.getOrDefault(priority, empty);
        if(!objects.equals(empty)) {
            hasKey = true;
        }
        objects.add(object);
        sortedObjects.put(priority, objects);
        if(!hasKey) {
            sortedObjects = new LinkedHashMap<>(new TreeMap<>(sortedObjects)); // Sort keys
        }
    }

    public void removeSortedObject(int priority, GameObject object) {
        List<GameObject> objects = sortedObjects.getOrDefault(priority, new ArrayList<>());
        objects.remove(object);
        sortedObjects.put(priority, objects);
    }

    public GameObject getGameObjectByName(String identifier) {
        return gameObjects.getOrDefault(identifier, null);
    }

    public Collection<GameObject> getGameObjects() {
        return gameObjects.values();
    }

    public Collection<GameObject> getActiveGameObjects() {
        Collection<GameObject> objects = new ArrayList<>();
        List<GameObject> gameObjects = new ArrayList<>(getGameObjects());
        for(GameObject object : gameObjects) {
            if(object.isActive()) {
                objects.add(object);
            }
        }
        return objects;
    }

    public LinkedHashMap<Integer, List<GameObject>> getSortedObjects() {
        return this.sortedObjects;
    }

    public void sortObjects() {
        sortedObjects = new LinkedHashMap<>();
        List<Integer> priorities = new ArrayList<>();
        int maxPriority = 0;
        for(GameObject go : getActiveGameObjects()) {
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
        for(GameObject go : getActiveGameObjects()) {
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
            for(GameObject go : getActiveGameObjects()) {
                for(ObjectComponent component : go.getComponents()) {
                    component.onStaticUpdate();
                }
            }
            window.getMouseListener().refresh();
            return;
        }
        for(GameObject go : getActiveGameObjects()) {
            for(ObjectComponent component : go.getComponents()) {
                component.onPreUpdate();
            }
        }
        for(GameObject go : getActiveGameObjects()) {
            for(ObjectComponent component : go.getComponents()) {
                component.onUpdate();
            }
        }
        for(GameObject go : getActiveGameObjects()) {
            for(ObjectComponent component : go.getComponents()) {
                component.onAfterUpdate();
            }
        }
        window.getMouseListener().refresh();
    }

}