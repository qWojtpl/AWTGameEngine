package pl.AWTGameEngine.objects.transform;

import physx.common.PxQuat;

public class QuaternionTransformSet {

    private double x = 0;
    private double y = 0;
    private double z = 0;
    private double w = 0;

    public QuaternionTransformSet() {

    }

    public QuaternionTransformSet(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public double getW() {
        return this.w;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setW(double w) {
        this.w = w;
    }

    public void clear() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 0;
    }

    public QuaternionTransformSet multiply(QuaternionTransformSet q) {
        double nW = this.w * q.w - this.x * q.x - this.y * q.y - this.z * q.z;
        double nX = this.w * q.x + this.x * q.w + this.y * q.z - this.z * q.y;
        double nY = this.w * q.y - this.x * q.z + this.y * q.w + this.z * q.x;
        double nZ = this.w * q.z + this.x * q.y - this.y * q.x + this.z * q.w;

        this.x = nX;
        this.y = nY;
        this.z = nZ;
        this.w = nW;

        return this;
    }

    public QuaternionTransformSet clone() {
        return new QuaternionTransformSet(this.x, this.y, this.z, this.w);
    }

    public QuaternionTransformSet deserializeFromToString(String data) {
        deserialize(data
                .replace("x=", "")
                .replace("y=", "")
                .replace("z=", "")
                .replace("w=", "")
                .replaceAll("\\[", "")
                .replaceAll("]", "")
                .replaceAll("QuaternionTransformSet", ""));
        return this;
    }

    public QuaternionTransformSet deserialize(String values) {
        String[] split = values.split(",");
        setX(Double.parseDouble(split[0]));
        setY(Double.parseDouble(split[1]));
        setZ(Double.parseDouble(split[2]));
        setW(Double.parseDouble(split[3]));
        return this;
    }

    public boolean equals(QuaternionTransformSet quaternionTransformSet) {
        if(quaternionTransformSet == null) {
            return false;
        }
        return this.x == quaternionTransformSet.getX() && this.y == quaternionTransformSet.getY() &&
                this.z == quaternionTransformSet.getZ() && this.w == quaternionTransformSet.getW();
    }

    public boolean isEmpty() {
        return this.x == 0 && this.y == 0 && this.z == 0 && this.w == 0;
    }

    public String toSimpleString() {
        return x + "," + y + "," + z + "," + w;
    }

    @Override
    public String toString() {
        return "QuaternionTransformSet[x=" + x + ",y=" + y + ",z=" + z + ",w=" + w + "]";
    }

    public static QuaternionTransformSet fromPhysX(PxQuat pxQuat) {
        return new QuaternionTransformSet(pxQuat.getX(), pxQuat.getY(), pxQuat.getZ(), pxQuat.getW());
    }

}
