package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Vector;

import java.util.ArrayList;
import java.util.List;

@Unique
public class PhysicsBody extends ObjectComponent {

    private double forceX = 0;
    private double forceY = 0;
    private double mass = 1;
    private final List<GameObject> lastObjects = new ArrayList<>();
    private int i = 0;

    public PhysicsBody(GameObject object) {
        super(object);
    }

    @Override
    public void onStaticUpdate() {
        onUpdate();
        //push(0, 16);
    }

    @Override
    public void onCollide(GameObject object) {
        if(lastObjects.contains(object)) {
            return;
        }
        List<ObjectComponent> components = object.getComponentsByClass(PhysicsBody.class);
        if(components.size() == 0) {
            return;
        }
        lastObjects.add(object);
        PhysicsBody body = (PhysicsBody) components.get(0);
        body.push(forceX / 8, forceY / 8);
    }

    @Override
    public void onUpdate() {
        updateX();
        updateY();
        i++;
        if(i == 2) {
            i = 0;
            lastObjects.clear();
        }
    }

    private void updateX() {
        if(forceX == 0) {
            return;
        }
        int move = (int) Math.floor(forceX / 8 / mass);
        if(forceX > 0) {
            if(!getObject().setX(getObject().getX() + move)) {
                forceX = (int) (forceX / 2);
                forceX = -forceX;
            } else {
                forceX -= 1;
            }
        } else if(forceX < 0) {
            if(!getObject().setX(getObject().getX() + move)) {
                forceX = (int) (forceX / 2);
                forceX = -forceX;
            } else {
                forceX += 1;
            }
        }
    }

    private void updateY() {
        if(forceY == 0) {
            return;
        }
        int move = (int) Math.floor(forceY / 8 / mass);
        System.out.println("M: " + move);
        System.out.println("F: " + forceX);
        if(forceY > 0) {
            if(!getObject().setY(getObject().getY() + move)) {
                forceY = (int) (forceY / 2);
                forceY = -forceY;
            } else {
                forceY -= 1;
            }
        } else if(forceY < 0) {
            if(!getObject().setY(getObject().getY() + move)) {
                forceY = (int) (forceY / 2);
                forceY = -forceY;
            } else {
                forceY += 1;
            }
        }
    }

    public void push(double force, Vector vector) {
        forceX += vector.getX() * force;
        forceY += vector.getY() * force;
    }

    public void push(double forceX, double forceY) {
        this.forceX += forceX;
        this.forceY += forceY;
    }

    public double getMass() {
        return this.mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void setMass(String mass) {
        try {
            setMass(Double.parseDouble(mass));
        } catch(NumberFormatException ignored) {}
    }

}
