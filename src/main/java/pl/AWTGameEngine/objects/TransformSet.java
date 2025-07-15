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

    public FloatableTransformSet getFloatable() {
        return new FloatableTransformSet(this);
    }

    public static class FloatableTransformSet {

        private final float x;
        private final float y;
        private final float z;

        public FloatableTransformSet(TransformSet transformSet) {
            this.x = (float) transformSet.getX();
            this.y = (float) transformSet.getY();
            this.z = (float) transformSet.getZ();
        }

        public float getX() {
            return this.x;
        }

        public float getY() {
            return this.y;
        }

        public float getZ() {
            return this.z;
        }

    }

}
