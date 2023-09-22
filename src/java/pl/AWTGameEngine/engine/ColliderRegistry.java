package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.components.BoxCollider;
import pl.AWTGameEngine.objects.GameObject;

import java.util.HashMap;

public class ColliderRegistry {

    private static HashMap<BoxCollider, GameObject> colliders = new HashMap<>();

    public static void registerCollider(BoxCollider collider, GameObject go) {
        colliders.put(collider, go);
    }

    public static boolean isColliding(GameObject object, int newX, int newY) {
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
            System.out.println(newX);
            System.out.println(go.getX() + c.getX1());
            System.out.println(go.getX() + c.getX2() + go.getScaleX());
            if(newX + object.getScaleX() >= go.getX() + c.getX1() && newX <= go.getX() + c.getX2() + go.getScaleX()
                && newY + object.getScaleY() >= go.getY() + c.getY1() && newY <= go.getY() + c.getY2() + go.getScaleY()) {
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
