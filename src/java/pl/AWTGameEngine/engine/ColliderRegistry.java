package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.components.BoxCollider;
import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ColliderRegistry {

    private final HashMap<BoxCollider, GameObject> colliders = new HashMap<>();
    private List<BoxCollider> lastColliders = new ArrayList<>();

    public void registerCollider(BoxCollider collider, GameObject go) {
        colliders.put(collider, go);
    }

    public boolean isColliding(GameObject object, BoxCollider collider, int newX, int newY) {
        if(object.getComponentsByClass(BoxCollider.class).size() == 0) {
            return false;
        }
        Path2D path = new Path2D.Double();
        for(int i = 0; i < 4; i++) {
            if(i == 0) {
                path.moveTo(collider.getPointsX().get(i) + newX - object.getX(), collider.getPointsY().get(i) + newY - object.getY());
            } else {
                path.lineTo(collider.getPointsX().get(i) + newX - object.getX(), collider.getPointsY().get(i) + newY - object.getY());
            }
        }
        path.closePath();
        Area baseArea = new Area(path);
        boolean colliding = false;
        List<GameObject> collided = new ArrayList<>();
        for(BoxCollider c : colliders.keySet()) {
            if(object.getComponentsByClass(BoxCollider.class).contains(c)) {
                continue;
            }
            if(collided.contains(c.getObject())) {
                continue;
            }
            Area colliderArea = new Area(c.getPath());
            Area clonedArea = (Area) baseArea.clone();
            clonedArea.intersect(colliderArea);
            if(!clonedArea.isEmpty()) {
                for(ObjectComponent component : object.getAbsoluteParent().getComponents()) {
                    component.onCollide(c.getObject());
                }
                collided.add(c.getObject());
                colliding = true;
            }
        }
        return colliding;
    }

    public List<BoxCollider> getLastColliders() {
        return new ArrayList<>(this.lastColliders);
    }

    public void clearRegistry() {
        colliders.clear();
    }

    public void removeCollider(BoxCollider collider) {
        colliders.remove(collider);
    }

}