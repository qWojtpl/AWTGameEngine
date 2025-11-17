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

    public TransformSet clone() {
        return new TransformSet(this.x, this.y, this.z);
    }

    public void clear() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
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

    public TransformSet setX(double x) {
        this.x = x;
        return this;
    }

    public TransformSet setY(double y) {
        this.y = y;
        return this;
    }

    @Platform3D
    public TransformSet setZ(double z) {
        this.z = z;
        return this;
    }

    @Override
    public String toString() {
        return "[TransformSet[x=" + x + ",y=" + y + ",z=" + z + "]]";
    }

    public TransformSet deserializeFromToString(String data) {
        deserialize(data
                .replace("x=", "")
                .replace("y=", "")
                .replace("z=", "")
                .replaceAll("\\[", "")
                .replaceAll("]", ""));
        return this;
    }

    public TransformSet deserialize(String values) {
        String[] split = values.split(",");
        if(split.length >= 2) {
            setX(Double.parseDouble(split[0]));
            setY(Double.parseDouble(split[1]));
        }
        if(split.length == 3) {
            setZ(Double.parseDouble(split[2]));
        }
        return this;
    }

    public double distanceTo(TransformSet transformSet) {
        double xR = Math.abs(this.x - transformSet.getX());
        double yR = Math.abs(this.y - transformSet.getX());
        double zR = Math.abs(this.z - transformSet.getX());
        return xR + yR + zR;
    }

    public boolean isEmpty() {
        return this.x == 0 && this.y == 0 && this.z == 0;
    }

    public boolean equals(TransformSet transformSet) {
        return this.x == transformSet.getX() && this.y == transformSet.getY() && this.z == transformSet.getZ();
    }

}
