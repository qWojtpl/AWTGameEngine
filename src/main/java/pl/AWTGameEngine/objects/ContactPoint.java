package pl.AWTGameEngine.objects;

public class ContactPoint {

    private final TransformSet position;
    private final TransformSet normal;
    private final TransformSet impulse;
    private final float separation;

    public ContactPoint(TransformSet position, TransformSet normal, TransformSet impulse, float separation) {
        this.position = position;
        this.normal = normal;
        this.impulse = impulse;
        this.separation = separation;
    }

    public TransformSet getPosition() {
        return this.position.clone();
    }

    public TransformSet getNormal() {
        return this.normal.clone();
    }

    public TransformSet getImpulse() {
        return this.impulse.clone();
    }

    public float getSeparation() {
        return this.separation;
    }

}
