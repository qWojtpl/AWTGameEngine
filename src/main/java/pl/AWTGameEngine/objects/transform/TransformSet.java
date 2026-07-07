package pl.AWTGameEngine.objects.transform;

import physx.common.PxVec3;
import pl.AWTGameEngine.components.base.ObjectComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class TransformSet {

    private double x = 0;
    private double y = 0;
    private double z = 0;
    private Consumer<List<ObjectComponent>> notifyAction;
    private List<ObjectComponent> excludeComponents;
    private final ReentrantLock lock = new ReentrantLock();

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

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    public TransformSet clone() {
        return new TransformSet(this.x, this.y, this.z);
    }

    public void clear() {
        lock.lock();
        try {
            this.x = 0;
            this.y = 0;
            this.z = 0;
            runNotify();
        } finally {
            lock.unlock();
        }
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

    public TransformSet setX(double x) {
        lock.lock();
        try {
            this.x = x;
            runNotify();
            return this;
        } finally {
            lock.unlock();
        }
    }

    public TransformSet setY(double y) {
        lock.lock();
        try {
            this.y = y;
            runNotify();
            return this;
        } finally {
            lock.unlock();
        }
    }

    public TransformSet setZ(double z) {
        lock.lock();
        try {
            this.z = z;
            runNotify();
            return this;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return "[TransformSet[x=" + x + ",y=" + y + ",z=" + z + "]]";
    }

    public String toSimpleString() {
        return x + "," + y + "," + z;
    }

    public TransformSet deserializeFromToString(String data) {
        deserialize(data
                .replace("x=", "")
                .replace("y=", "")
                .replace("z=", "")
                .replaceAll("\\[", "")
                .replaceAll("]", "")
                .replaceAll("TransformSet", ""));
        return this;
    }

    public TransformSet deserialize(String values) {
        lock.lock();
        try {
            String[] split = values.split(",");
            if (split.length >= 2) {
                this.x = Double.parseDouble(split[0]);
                this.y = Double.parseDouble(split[1]);
            }
            if (split.length == 3) {
                this.z = Double.parseDouble(split[2]);
            }
            runNotify();
            return this;
        } finally {
            lock.unlock();
        }
    }

    public double distanceTo(TransformSet transformSet) {
        double xR = Math.pow(this.x - transformSet.getX(), 2);
        double yR = Math.pow(this.y - transformSet.getY(), 2);
        double zR = Math.pow(this.z - transformSet.getZ(), 2);
        return Math.sqrt(xR + yR + zR);
    }

    public boolean isEmpty() {
        return this.x == 0 && this.y == 0 && this.z == 0;
    }

    public boolean equals(TransformSet transformSet) {
        if(transformSet == null) {
            return false;
        }
        return this.x == transformSet.getX() && this.y == transformSet.getY() && this.z == transformSet.getZ();
    }

    public TransformSet fromPhysX(PxVec3 pxVec3) {
        lock.lock();
        try {
            this.x = pxVec3.getX();
            this.y = pxVec3.getY();
            this.z = pxVec3.getZ();
            runNotify();
            return this;
        } finally {
            lock.unlock();
        }
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
