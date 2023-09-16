package pl.AWTGameEngine.scenes;

import pl.AWTGameEngine.objects.GameObject;

import java.util.Collection;
import java.util.HashMap;

public class Scene {

    private final String name;
    private final HashMap<String, GameObject> gameObjects = new HashMap<>();

    public Scene(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public GameObject createGameObject(String identifier) throws DuplicatedObjectException {
        if(gameObjects.containsKey(identifier)) {
            throw new DuplicatedObjectException(identifier);
        }
        GameObject go = new GameObject();
        gameObjects.put(identifier, go);
        return go;
    }

    public Collection<GameObject> getGameObjects() {
        return gameObjects.values();
    }

}
