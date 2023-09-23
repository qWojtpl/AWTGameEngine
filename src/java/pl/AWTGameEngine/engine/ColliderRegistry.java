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
            if(newX + collider.getX2() + object.getScaleX() >= go.getX() + c.getX1() && newX + collider.getX1() <= go.getX() + c.getX2() + go.getScaleX()
                && newY - collider.getY1() + object.getScaleY() >= go.getY() + c.getY1() && newY + collider.getY1() <= go.getY() + c.getY2() + go.getScaleY()) {
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
