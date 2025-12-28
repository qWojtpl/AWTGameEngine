package pl.AWTGameEngine.objects;

import physx.common.PxQuat;
import physx.common.PxVec3;

public class QuaternionTransformSet {

    private double x;
    private double y;
    private double z;
    private double w;

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

    public QuaternionTransformSet clone() {
        return new QuaternionTransformSet(this.x, this.y, this.z, this.w);
    }

    @Override
    public String toString() {
        return "QuaternionTransformSet[x=" + x + ",y=" + y + ",z=" + z + ",w=" + w + "]";
    }

    public static QuaternionTransformSet fromPhysX(PxQuat pxQuat) {
        return new QuaternionTransformSet(pxQuat.getX(), pxQuat.getY(), pxQuat.getZ(), pxQuat.getW());
    }

}
