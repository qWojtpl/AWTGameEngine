package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.components.BoxCollider;
import pl.AWTGameEngine.objects.GameObject;

import java.util.HashMap;

public class ColliderRegistry {

    private static HashMap<BoxCollider, GameObject> colliders = new HashMap<>();

    public static void registerCollider(BoxCollider collider, GameObject go) {
        colliders.put(collider, go);
    }

    public static boolean isColliding(BoxCollider collider, int newX, int newY) {
        for(BoxCollider c : colliders.keySet()) {
            if(c.equals(collider)) {
                continue;
            }
            GameObject go = getColliderObject(c);
            if(go == null) {
                continue;
            }
            if(newX + go.getScaleX() >= c.getX1() + go.getX() - go.getScaleX() && newX <= c.getX2() + go.getX() + go.getScaleX()
                && newY + go.getScaleY() >= c.getY1() + go.getY() - go.getScaleY() && newY <= c.getY2() + go.getY() + go.getScaleY()) {
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
