package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Vector;

@Unique
public class PhysicsBody extends ObjectComponent {

    private Vector vector = new Vector(0, 0);
    private double mass = 20;

    public PhysicsBody(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {

    }

    @Override
    public void onPreUpdate() {
        if(vector.getX() != 0) {
            getObject().moveX(getObject().getX() + subtractVectorX());
        }
        if(vector.getY() != 0) {
            getObject().moveY(getObject().getY() + subtractVectorY());
        }
    }

    @Override
    public void onStaticUpdate() {
        onPreUpdate();
    }

    @Override
    public void onCollide(GameObject collider) {
        if(collider.hasComponent(PhysicsBody.class)) {
            for(ObjectComponent component : collider.getComponentsByClass(PhysicsBody.class)) {
                PhysicsBody physicsBody = (PhysicsBody) component;
                physicsBody.push(new Vector(vector.getX() / 2, vector.getY() / 2));
            }
        }
        vector = new Vector(-vector.getX() / 2, -vector.getY() / 2);
    }

    public void push(Vector vector) {
        this.vector.setX(this.vector.getX() + vector.getX());
        this.vector.setY(this.vector.getY() + vector.getY());
    }

    private int subtractVectorX() {
        if(vector.getX() < 0) {
            vector.setX((int) Math.ceil(vector.getX() + 1 / mass));
        } else {
            vector.setX((int) Math.ceil(vector.getX() - 1 / mass));
        }
        return vector.getX();
    }

    private int subtractVectorY() {
        if(vector.getY() < 0) {
            vector.setY((int) Math.ceil(vector.getY() + 1 / mass));
        } else {
            vector.setY((int) Math.ceil(vector.getY() - 1 / mass));
        }
        return vector.getY();
    }

}
