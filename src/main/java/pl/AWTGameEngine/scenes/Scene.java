package pl.AWTGameEngine.scenes;

import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.*;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import java.util.*;

public class Scene {

    private final String name;
    private final LinkedHashMap<String, GameObject> gameObjects = new LinkedHashMap<>();
    private final Window window;
    private ColliderRegistry colliderRegistry;
    private EventHandler sceneEventHandler;
    private String customStyles;

    public Scene(String name, Window window) {
        this.name = name;
        this.window = window;
        setColliderRegistry(new ColliderRegistry());
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

    public EventHandler getSceneEventHandler() {
        return this.sceneEventHandler;
    }

    public String getCustomStyles() {
        return this.customStyles;
    }

    public void setColliderRegistry(ColliderRegistry registry) {
        this.colliderRegistry = registry;
    }

    public void setSceneEventHandler(EventHandler sceneEventHandler) {
        this.sceneEventHandler = sceneEventHandler;
    }

    public void setCustomStyles(String styles) {
        this.customStyles = styles;
    }

    public GameObject createGameObject(String identifier) {
        GameObject go = new GameObject(identifier, this);
        try {
            addGameObject(go);
        } catch(RuntimeException e) {
            return null;
        }
        return go;
    }

    public void addGameObject(GameObject object) {
        if(object.getIdentifier().contains("$")) {
            String errorMsg = "Cannot add object with identifier which contains $ sign - this sign is reserved.";
            Logger.log(1, errorMsg);
            throw new RuntimeException(errorMsg);
        }
        if(gameObjects.containsKey(object.getIdentifier())) {
            String errorMsg = "Cannot add object with identifier "
                    + object.getIdentifier() + ", object with this identifier already exists in this scene.";
            Logger.log(1, errorMsg);
            throw new RuntimeException(errorMsg);
        }
        if(!this.equals(object.getScene())) {
            String errorMsg = "Cannot add object with identifier"
                    + object.getIdentifier() + ", object's scene is not set to this scene";
            Logger.log(1, errorMsg);
            throw new RuntimeException(errorMsg);
        }
        object.setPanel(window.getPanel());
        gameObjects.put(object.getIdentifier(), object);
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
        gameObjects.remove(object.getIdentifier());
        for(GameObject obj : getActiveGameObjects()) {
            if(obj.equals(object)) {
                continue;
            }
            for(ObjectComponent component : obj.getEventHandler().getComponents("onRemoveGameObject#GameObject")) {
                component.onRemoveGameObject(object);
            }
        }
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
        window.getPanel().getMouseListener().refresh();
    }

}