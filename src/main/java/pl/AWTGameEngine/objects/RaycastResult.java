package pl.AWTGameEngine.objects;

import pl.AWTGameEngine.objects.transform.Vector3;

public class RaycastResult {

    private GameObject object;
    private Vector3 position;
    private double distance;

    public GameObject getObject() {
        return this.object;
    }

    public Vector3 getPosition() {
        return this.position;
    }

    public double getDistance() {
        return this.distance;
    }

    public void setObject(GameObject object) {
        this.object = object;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

}
