package pl.AWTGameEngine.scenes;

import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.engine.*;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class Scene {

    private final String name;
    private final LinkedHashMap<String, GameObject> gameObjects = new LinkedHashMap<>();
    private LinkedHashMap<Integer, List<GameObject>> sortedObjects = new LinkedHashMap<>();
    private final Window window;
    private ColliderRegistry colliderRegistry;
    private PanelRegistry panelRegistry;
    private EventHandler sceneEventHandler;

    public Scene(String name, Window window) {
        this.name = name;
        this.window = window;
        setColliderRegistry(new ColliderRegistry());
        setPanelRegistry(new PanelRegistry());
        setSceneEventHandler(new EventHandler());
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

    public EventHandler getSceneEventHandler() {
        return this.sceneEventHandler;
    }

    public void setColliderRegistry(ColliderRegistry registry) {
        this.colliderRegistry = registry;
    }

    public void setPanelRegistry(PanelRegistry registry) {
        this.panelRegistry = registry;
    }

    public void setSceneEventHandler(EventHandler sceneEventHandler) {
        this.sceneEventHandler = sceneEventHandler;
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
            for(ObjectComponent component : obj.getEventHandler().getComponents("onCreateGameObject#GameObject")) {
                component.onCreateGameObject(object);
            }
        }
    }

    public void removeGameObject(GameObject object) {
        if(object == null) {
            return;
        }
        if(object.isActive()) {
            for(ObjectComponent component : object.getComponents()) {
                object.removeComponent(component);
            }
        }
        for(GameObject child : object.getChildren()) {
            child.setParent(object.getParent());
        }
        object.setParent(null);
        gameObjects.remove(object.getIdentifier());
        removeSortedObject(object.getPriority(), object);
        BindingsManager.removeBindingsByOwner(object);
        for(GameObject obj : getActiveGameObjects()) {
            if(obj.equals(object)) {
                continue;
            }
            for(ObjectComponent component : obj.getEventHandler().getComponents("onRemoveGameObject#GameObject")) {
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

    public List<GameObject> getGameObjects() {
        return new ArrayList<>(gameObjects.values());
    }

    public List<GameObject> getActiveGameObjects() {
        List<GameObject> objects = new ArrayList<>();
        List<GameObject> gameObjects = new ArrayList<>(getGameObjects());
        for(GameObject object : gameObjects) {
            if(object.isActive()) {
                objects.add(object);
            }
        }
        return objects;
    }

    public LinkedHashMap<Integer, List<GameObject>> getSortedObjects() {
        LinkedHashMap<Integer, List<GameObject>> sorted = new LinkedHashMap<>();
        for(int key : sortedObjects.keySet()) {
            sorted.put(key, new ArrayList<>(sortedObjects.get(key)));
        }
        return sorted;
    }

    public void removeAllObjects() {
        for(GameObject object : getGameObjects()) {
            removeGameObject(object);
        }
    }

    public void update() {
        if(window.isStaticMode()) {
            for(ObjectComponent component : sceneEventHandler.getComponents("onStaticUpdate")) {
                component.onStaticUpdate();
            }
        } else {
            for(ObjectComponent component : sceneEventHandler.getComponents("onPreUpdate")) {
                component.onPreUpdate();
            }
            for(ObjectComponent component : sceneEventHandler.getComponents("onUpdate")) {
                component.onUpdate();
            }
            for(ObjectComponent component : sceneEventHandler.getComponents("onAfterUpdate")) {
                component.onAfterUpdate();
            }
        }
        BindingsManager.updateBindings();
        for(NestedPanel panel : panelRegistry.getPanels()) {
            panel.getMouseListener().refresh();
        }
    }

    public void saveSceneState(String path) {
        saveSceneState(path, getGameObjects());
    }

    public void saveSceneState(String path, List<GameObject> objects) {
        StringBuilder serializationString = new StringBuilder();
        for(GameObject object : objects) {
            if(object.getIdentifier().startsWith("@")) {
                continue;
            }
            serializationString.append(object.getIdentifier());
            serializationString.append("=\"");
            serializationString.append(object.getSerializeString());
            serializationString.append("\";");
            serializationString.append("\n");
        }
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file, false);
            writer.write(serializationString.toString());
            writer.close();
        } catch(Exception e) {
            Logger.log("Cannot save current scene state", e);
        }
        Logger.log(2, "Saved " + getName() + " scene state to file " + path);
    }

}