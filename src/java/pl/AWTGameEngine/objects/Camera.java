package pl.AWTGameEngine.objects;

import pl.AWTGameEngine.components.Canvas;
import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.engine.panels.PanelObject;

import java.util.List;

public class Camera {

    private final PanelObject panel;
    private int x = 0;
    private int y = 0;
    private float zoom = 1;

    public Camera(PanelObject panel) {
        this.panel = panel;
        setZoom((float) (panel.getWindow().getMultiplier() / 2f));
    }

    public int parseX(GameObject object, int value) {
        return parse(value, getRelativeX(object));
    }

    public int parseY(GameObject object, int value) {
        return parse(value, getRelativeY(object));
    }

    private int parse(int value, int relative) {
        return Math.round((value - relative) * getZoom());
    }

    public int parseScale(int scale) {
        return Math.round(scale * getZoom());
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

    public float getZoom() {
        return this.zoom;
    }

    public void setX(int x) {
        this.x = x;
        updatedPosition();
    }

    public void setY(int y) {
        this.y = y;
        updatedPosition();
    }

    public void setBounds(int x, int y) {
        this.x = x;
        this.y = y;
        updatedPosition();
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    private void updatedPosition() {
        List<ObjectComponent> components = panel.getWindow().getCurrentScene()
                .getSceneEventHandler().getComponents("onUpdateCameraPosition#int#int");
        for(ObjectComponent component : components) {
            component.onUpdateCameraPosition(this.x, this.y);
        }
    }

}
