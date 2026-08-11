package pl.AWTGameEngine.objects.transform;

import physx.common.PxVec3;
import pl.AWTGameEngine.components.base.ObjectComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class Vector3 {

    private double x = 0;
    private double y = 0;
    private double z = 0;
    private Consumer<List<ObjectComponent>> notifyAction;
    private List<ObjectComponent> excludeComponents;
    private final ReentrantLock lock = new ReentrantLock();

    public Vector3() {

    }

    public Vector3(double x) {
        this.x = x;
    }

    public Vector3(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    public Vector3 clone() {
        return new Vector3(this.x, this.y, this.z);
    }

    public void clear() {
        lock();
        try {
            this.x = 0;
            this.y = 0;
            this.z = 0;
        } finally {
            unlock();
        }
        runNotify();
    }

    public synchronized double getX() {
        return this.x;
    }

    public synchronized double getY() {
        return this.y;
    }

    public synchronized double getZ() {
        return this.z;
    }

    public Vector3 setX(double x) {
        lock();
        try {
            this.x = x;
        } finally {
            unlock();
        }
        runNotify();
        return this;
    }

    public Vector3 setY(double y) {
        lock();
        try {
            this.y = y;
        } finally {
            unlock();
        }
        runNotify();
        return this;
    }

    public Vector3 setZ(double z) {
        lock();
        try {
            this.z = z;
        } finally {
            unlock();
        }
        runNotify();
        return this;
    }

    public Vector3 set(double x, double y, double z) {
        lock();
        try {
            this.x = x;
            this.y = y;
            this.z = z;
        } finally {
            unlock();
        }
        runNotify();
        return this;
    }

    public Vector3 add(Vector3 vector3) {
        lock();
        try {
            this.x = x + vector3.getX();
            this.y = y + vector3.getY();
            this.z = z + vector3.getZ();
        } finally {
            unlock();
        }
        runNotify();
        return this;
    }

    @Override
    public synchronized String toString() {
        return "[Vector3[x=" + x + ",y=" + y + ",z=" + z + "]]";
    }

    public synchronized String toSimpleString() {
        return x + "," + y + "," + z;
    }

    public Vector3 deserializeFromToString(String data) {
        deserialize(data
                .replace("x=", "")
                .replace("y=", "")
                .replace("z=", "")
                .replaceAll("\\[", "")
                .replaceAll("]", "")
                .replaceAll("Vector3", ""));
        return this;
    }

    public Vector3 deserialize(String values) {
        lock();
        try {
            String[] split = values.split(",");
            if(split.length >= 2) {
                this.x = Double.parseDouble(split[0]);
                this.y = Double.parseDouble(split[1]);
            }
            if(split.length == 3) {
                this.z = Double.parseDouble(split[2]);
            }
        } finally {
            unlock();
        }
        runNotify();
        return this;
    }

    public double distanceTo(Vector3 vector3) {
        double xR = Math.pow(this.x - vector3.getX(), 2);
        double yR = Math.pow(this.y - vector3.getY(), 2);
        double zR = Math.pow(this.z - vector3.getZ(), 2);
        return Math.sqrt(xR + yR + zR);
    }

    public boolean isEmpty() {
        return this.x == 0 && this.y == 0 && this.z == 0;
    }

    public boolean equals(Vector3 vector3) {
        if(vector3 == null) {
            return false;
        }
        return this.x == vector3.getX() && this.y == vector3.getY() && this.z == vector3.getZ();
    }

    public Vector3 fromPhysX(PxVec3 pxVec3) {
        lock();
        try {
            this.x = pxVec3.getX();
            this.y = pxVec3.getY();
            this.z = pxVec3.getZ();
        } finally {
            unlock();
        }
        runNotify();
        return this;
    }

    public static Vector3 createForwardVector(Vector3 rotation) {
        double pitch = Math.toRadians(rotation.getX());
        double yaw = Math.toRadians(rotation.getY());

        return new Vector3(
                Math.cos(pitch) * Math.sin(yaw),
                Math.sin(pitch),
                -Math.cos(pitch) * Math.cos(yaw)
        ).normalize();
    }

    public Vector3 normalize() {
        double l = Math.sqrt(x * x + y * y + z * z);
        if(l > 0.00001) {
            set(x / l, y / l, z / l);
        } else {
            set(0, 0, 0);
        }
        return this;
    }

    public Consumer<List<ObjectComponent>> getNotifyAction() {
        return this.notifyAction;
    }

    public void setNotifyAction(Consumer<List<ObjectComponent>> action) {
        this.notifyAction = action;
    }

    public void addNotifyExcludeComponent(ObjectComponent component) {
        if(this.excludeComponents == null) {
            this.excludeComponents = new ArrayList<>();
        }
        this.excludeComponents.add(component);
    }

    private void runNotify() {
        if(this.notifyAction == null) {
            return;
        }
        this.notifyAction.accept(this.excludeComponents);
        this.excludeComponents = null;
    }

}
