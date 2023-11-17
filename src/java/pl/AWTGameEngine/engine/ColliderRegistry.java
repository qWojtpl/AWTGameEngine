package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.components.Collider;
import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class ColliderRegistry {

    private final List<Collider> colliders = new ArrayList<>();

    public void registerCollider(Collider collider) {
        colliders.add(collider);
    }

    public boolean isColliding(GameObject object, Collider collider, int newX, int newY) {
        List<Collider> colliderComponents = new ArrayList<>();
        for(ObjectComponent component : object.getComponents()) {
            if(!(component instanceof Collider)) {
                continue;
            }
            colliderComponents.add((Collider) component);
        }
        if(colliderComponents.size() == 0) {
            return false;
        }
        Path2D path = collider.calculatePath(newX, newY);
        path.closePath();
        Area baseArea = new Area(path);
        boolean colliding = false;
        List<GameObject> collided = new ArrayList<>();
        for(Collider c : colliders) {
            if(colliderComponents.contains(c)) {
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

    public void clearRegistry() {
        colliders.clear();
    }

    public void removeCollider(Collider collider) {
        colliders.remove(collider);
    }

}