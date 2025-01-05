package pl.AWTGameEngine.objects;

import pl.AWTGameEngine.annotations.Platform3D;

public class TransformSet {

    private int x = 0;
    private int y = 0;
    private int z = 0;

    public TransformSet() {

    }

    public TransformSet(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Platform3D
    public int getZ() {
        return this.z;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Platform3D
    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "[TransformSet[x=" + x + ",y=" + y + ",z=" + z + "]]";
    }

}
