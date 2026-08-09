package pl.AWTGameEngine.objects.transform;

import physx.common.PxQuat;

public class Vector4 {

    private double x = 0;
    private double y = 0;
    private double z = 0;
    private double w = 1;

    public Vector4() {

    }

    public Vector4(double x, double y, double z, double w) {
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
        this.w = 1;
    }

    public Vector4 multiply(Vector4 q) {
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

    public Vector4 clone() {
        return new Vector4(this.x, this.y, this.z, this.w);
    }

    public Vector4 deserializeFromToString(String data) {
        deserialize(data
                .replace("x=", "")
                .replace("y=", "")
                .replace("z=", "")
                .replace("w=", "")
                .replaceAll("\\[", "")
                .replaceAll("]", "")
                .replaceAll("Vector4", ""));
        return this;
    }

    public Vector4 deserialize(String values) {
        String[] split = values.split(",");
        setX(Double.parseDouble(split[0]));
        setY(Double.parseDouble(split[1]));
        setZ(Double.parseDouble(split[2]));
        setW(Double.parseDouble(split[3]));
        return this;
    }

    public boolean equals(Vector4 vector4) {
        if(vector4 == null) {
            return false;
        }
        return this.x == vector4.getX() && this.y == vector4.getY() &&
                this.z == vector4.getZ() && this.w == vector4.getW();
    }

    public boolean isEmpty() {
        return this.x == 0 && this.y == 0 && this.z == 0 && this.w == 1;
    }

    public String toSimpleString() {
        return x + "," + y + "," + z + "," + w;
    }

    @Override
    public String toString() {
        return "Vector4[x=" + x + ",y=" + y + ",z=" + z + ",w=" + w + "]";
    }

    public static Vector4 fromPhysX(PxQuat pxQuat) {
        return new Vector4(pxQuat.getX(), pxQuat.getY(), pxQuat.getZ(), pxQuat.getW());
    }

}
