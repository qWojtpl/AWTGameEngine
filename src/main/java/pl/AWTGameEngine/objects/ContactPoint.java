package pl.AWTGameEngine.objects;

import pl.AWTGameEngine.objects.transform.Vector3;

public class ContactPoint {

    private final Vector3 position;
    private final Vector3 normal;
    private final Vector3 impulse;
    private final float separation;

    public ContactPoint(Vector3 position, Vector3 normal, Vector3 impulse, float separation) {
        this.position = position;
        this.normal = normal;
        this.impulse = impulse;
        this.separation = separation;
    }

    public Vector3 getPosition() {
        return this.position.clone();
    }

    public Vector3 getNormal() {
        return this.normal.clone();
    }

    public Vector3 getImpulse() {
        return this.impulse.clone();
    }

    public float getSeparation() {
        return this.separation;
    }

}
