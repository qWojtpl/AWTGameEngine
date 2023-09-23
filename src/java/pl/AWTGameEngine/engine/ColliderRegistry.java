package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.components.BoxCollider;
import pl.AWTGameEngine.objects.GameObject;

import java.util.HashMap;

public class ColliderRegistry {

    private static final HashMap<BoxCollider, GameObject> colliders = new HashMap<>();

    public static void registerCollider(BoxCollider collider, GameObject go) {
        colliders.put(collider, go);
    }

    public static boolean isColliding(GameObject object, BoxCollider collider, int newX, int newY) {
        if(object.getComponentsByClass(BoxCollider.class).size() == 0) {
            return false;
        }
        for(BoxCollider c : colliders.keySet()) {
            if(object.getComponentsByClass(BoxCollider.class).contains(c)) {
                continue;
            }
            GameObject go = getColliderObject(c);
            if(go == null) {
                continue;
            }
            if(newX + object.getScaleX() + collider.getX() + collider.getScaleX() > go.getX() + c.getX()
                && newX + collider.getX() < go.getX() + go.getScaleX() + c.getX() + c.getScaleX()
                && newY + object.getScaleY() + collider.getY() + collider.getScaleY() > go.getY() + c.getY()
                && newY + collider.getY() < go.getY() + go.getScaleY() + c.getY() + c.getScaleY()) {
                return true;
            }
        }
        return false;
    }

    public static GameObject getColliderObject(BoxCollider collider) {
        return colliders.getOrDefault(collider, null);
    }

    public static void clearRegistry() {
        colliders.clear();
    }

}
