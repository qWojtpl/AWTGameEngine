package pl.AWTGameEngine.scenes;

import pl.AWTGameEngine.annotations.Command;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.*;
import pl.AWTGameEngine.engine.panels.PanelObject;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Scene {

    private final String name;
    private final ConcurrentHashMap<String, GameObject> gameObjects = new ConcurrentHashMap<>();
    private final Window window;
    private final RenderEngine renderEngine;
    private PanelObject panel;
    private ColliderRegistry colliderRegistry;
    private EventHandler sceneEventHandler;
    private String customStyles = "";
    private final HashMap<String, RenderEngine> loadAfterLoad = new HashMap<>();

    public Scene(String name, Window window, RenderEngine renderEngine) {
        this.name = name;
        this.window = window;
        this.renderEngine = renderEngine;
        setColliderRegistry(new ColliderRegistry());
        setSceneEventHandler(new EventHandler());
    }

    @Command("name")
    public String getName() {
        return this.name;
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

    public void setPanel(PanelObject panel) {
        this.panel = panel;
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
            Logger.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        if(gameObjects.containsKey(object.getIdentifier())) {
            String errorMsg = "Cannot add object with identifier "
                    + object.getIdentifier() + ", object with this identifier already exists in this scene.";
            Logger.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        if(!this.equals(object.getScene())) {
            String errorMsg = "Cannot add object with identifier"
                    + object.getIdentifier() + ", object's scene is not set to this scene";
            Logger.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        object.setPanel(panel);
        gameObjects.put(object.getIdentifier(), object);
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

    public void loadAfterLoad(String source, RenderEngine renderEngine) {
        loadAfterLoad.put(source, renderEngine);
    }

    public void triggerAfterLoad() {
        int c = loadAfterLoad.keySet().size();
        for(String path : loadAfterLoad.keySet()) {
            getWindow().getSceneLoader().loadSceneFile(path, loadAfterLoad.get(path), true);
        }
        if(c > 0) {
            getWindow().setCurrentScene(this);
            loadAfterLoad.clear();
        }
    }

    public PanelObject getPanel() {
        return this.panel;
    }

    public Window getWindow() {
        return this.window;
    }

    public RenderEngine getRenderEngine() {
        return this.renderEngine;
    }

    public void update() {
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

    public void netUpdate() {
        for(ObjectComponent component : sceneEventHandler.getComponents("onNetUpdate")) {
            component.onNetUpdate();
        }
    }

    public void updateSecond() {
        for(ObjectComponent component : sceneEventHandler.getComponents("onEverySecond")) {
            component.onEverySecond();
        }
    }

}