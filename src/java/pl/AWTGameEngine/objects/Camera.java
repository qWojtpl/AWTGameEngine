package pl.AWTGameEngine.objects;

import pl.AWTGameEngine.components.Canvas;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.panels.Panel3D;
import pl.AWTGameEngine.engine.panels.PanelObject;

import java.util.List;

public class Camera {

    private final PanelObject panel;
    private int x = 0;
    private int y = 0;
    private int z = 0;
    private final TransformSet rotation = new TransformSet();

    public Camera(PanelObject panel) {
        this.panel = panel;
    }

    public int parseX(GameObject object, int value) {
        return parse(value, getRelativeX(object));
    }

    public int parseY(GameObject object, int value) {
        return parse(value, getRelativeY(object));
    }

    public int parseZ(GameObject object, int value) {
        return parse(value, getRelativeZ(object));
    }

    private int parse(int value, int relative) {
        return Math.round((value - relative) * getMultiplier());
    }

    public int parsePlainValue(int value) {
        return Math.round(value * getMultiplier());
    }

    public PanelObject getPanel() {
        return this.panel;
    }

    public int getX() {
        return this.x;
    }

    public int getRelativeX(GameObject object) {
        if(object.getComponentsByClass(Canvas.class).isEmpty()) {
            return 0;
        }
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getRelativeY(GameObject object) {
        if(object.getComponentsByClass(Canvas.class).isEmpty()) {
            return 0;
        }
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public int getRelativeZ(GameObject object) {
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
        return ((float) panel.getWindow().getWidth() / panel.getWindow().getBaseWidth());
    }

    public void setX(int x) {
        this.x = x;
        updatedPosition();
    }

    public void setY(int y) {
        this.y = y;
        updatedPosition();
    }

    public void setZ(int z) {
        this.z = z;
        updatedPosition();
    }

    public void setBounds(int x, int y) {
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

    public void setRotationX(int x) {
        this.rotation.setX(x);
        updatedRotation();
    }

    public void setRotationY(int y) {
        this.rotation.setY(y);
        updatedRotation();
    }

    public void setRotationZ(int z) {
        this.rotation.setZ(z);
        updatedRotation();
    }

    private void updatedPosition() {
        updateCamera3D();
        List<ObjectComponent> components = panel.getWindow().getCurrentScene()
                .getSceneEventHandler().getComponents("onUpdateCameraPosition#int#int");
        for(ObjectComponent component : components) {
            component.onUpdateCameraPosition(this.x, this.y);
        }
    }

    private void updatedPositionZ() {
        updateCamera3D();
        List<ObjectComponent> components = panel.getWindow().getCurrentScene()
                .getSceneEventHandler().getComponents("onUpdateCameraPosition#int#int#int");
        for(ObjectComponent component : components) {
            component.onUpdateCameraPosition(this.x, this.y, this.z);
        }
    }

    private void updatedRotation() {
        updateCamera3D();
        List<ObjectComponent> components = panel.getWindow().getCurrentScene()
                .getSceneEventHandler().getComponents("onUpdateCameraRotation#int#int#int");
        for(ObjectComponent component : components) {
            component.onUpdateCameraRotation(this.rotation.getX(), this.rotation.getY(), this.rotation.getZ());
        }
    }

    private void updateCamera3D() {
        if(panel instanceof Panel3D) {
            ((Panel3D) panel).updateCamera3D();
        }
    }

}
