package pl.AWTGameEngine.objects;

import pl.AWTGameEngine.components.Canvas;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.panels.Panel3D;
import pl.AWTGameEngine.engine.panels.PanelObject;

import java.util.List;

public class Camera {

    private final PanelObject panel;
    private double x = 0;
    private double y = 0;
    private double z = 0;
    private final TransformSet rotation = new TransformSet();

    public Camera(PanelObject panel) {
        this.panel = panel;
    }

    public double parseX(GameObject object, double value) {
        return parse(value, getRelativeX(object));
    }

    public double parseY(GameObject object, double value) {
        return parse(value, getRelativeY(object));
    }

    public double parseZ(GameObject object, double value) {
        return parse(value, getRelativeZ(object));
    }

    private double parse(double value, double relative) {
        return Math.round((value - relative) * getMultiplier());
    }

    public double parsePlainValue(double value) {
        return Math.round(value * getMultiplier());
    }

    public PanelObject getPanel() {
        return this.panel;
    }

    public double getX() {
        return this.x;
    }

    public double getRelativeX(GameObject object) {
        if(object.getComponentsByClass(Canvas.class).isEmpty()) {
            return 0;
        }
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getRelativeY(GameObject object) {
        if(object.getComponentsByClass(Canvas.class).isEmpty()) {
            return 0;
        }
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public double getRelativeZ(GameObject object) {
        if(object.getComponentsByClass(Canvas.class).isEmpty()) {
            return 0;
        }
        return this.y;
    }

    public TransformSet getRotation() {
        return new TransformSet(rotation.getX(), rotation.getY(), rotation.getZ());
    }

    public float getMultiplier() {
        if(!panel.getWindow().isSameSize()) {
            return 1;
        }
        return ((float) panel.getSize().getWidth() / panel.getWindow().getBaseWidth());
    }

    public void setX(double x) {
        this.x = x;
        updatedPosition();
    }

    public void setY(double y) {
        this.y = y;
        updatedPosition();
    }

    public void setZ(double z) {
        this.z = z;
        updatedPosition();
    }

    public void setBounds(double x, double y) {
        this.x = x;
        this.y = y;
        updatedPosition();
    }

    public void setPosition(TransformSet transform) {
        this.x = transform.getX();
        this.y = transform.getY();
        this.z = transform.getZ();
        updatedPositionZ();
    }

    public void setRotation(TransformSet transform) {
        this.rotation.setX(transform.getX());
        this.rotation.setY(transform.getY());
        this.rotation.setZ(transform.getZ());
        updatedRotation();
    }

    public void setRotationX(double x) {
        this.rotation.setX(x);
        updatedRotation();
    }

    public void setRotationY(double y) {
        this.rotation.setY(y);
        updatedRotation();
    }

    public void setRotationZ(double z) {
        this.rotation.setZ(z);
        updatedRotation();
    }

    private void updatedPosition() {
        List<ObjectComponent> components = panel.getWindow().getCurrentScene()
                .getSceneEventHandler().getComponents("onUpdateCameraPosition#int#int");
        for(ObjectComponent component : components) {
            component.onUpdateCameraPosition(this.x, this.y);
        }
    }

    private void updatedPositionZ() {
        List<ObjectComponent> components = panel.getWindow().getCurrentScene()
                .getSceneEventHandler().getComponents("onUpdateCameraPosition#int#int#int");
        for(ObjectComponent component : components) {
            component.onUpdateCameraPosition(this.x, this.y, this.z);
        }
    }

    private void updatedRotation() {
        List<ObjectComponent> components = panel.getWindow().getCurrentScene()
                .getSceneEventHandler().getComponents("onUpdateCameraRotation#int#int#int");
        for(ObjectComponent component : components) {
            component.onUpdateCameraRotation(this.rotation.getX(), this.rotation.getY(), this.rotation.getZ());
        }
    }

}
