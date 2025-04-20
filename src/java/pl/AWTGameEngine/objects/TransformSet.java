package pl.AWTGameEngine.objects;

import pl.AWTGameEngine.annotations.Platform3D;

public class TransformSet {

    private double x = 0;
    private double y = 0;
    private double z = 0;

    public TransformSet() {

    }

    public TransformSet(double x) {
        this.x = x;
    }

    public TransformSet(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public TransformSet(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    @Platform3D
    public double getZ() {
        return this.z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Platform3D
    public void setZ(double z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "[TransformSet[x=" + x + ",y=" + y + ",z=" + z + "]]";
    }

}
