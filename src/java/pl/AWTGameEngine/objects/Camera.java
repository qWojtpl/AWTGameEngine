package pl.AWTGameEngine.objects;

import pl.AWTGameEngine.components.Canvas;
import pl.AWTGameEngine.engine.NestedPanel;

public class Camera {

    private final NestedPanel panel;
    private int x = 0;
    private int y = 0;
    private float zoom = 1;

    public Camera(NestedPanel panel) {
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
        return (int) ((value - relative) * getZoom());
    }

    public int parseScale(int scale) {
        return (int) (scale * getZoom());
    }

    public NestedPanel getPanel() {
        return this.panel;
    }

    public int getX() {
        return this.x;
    }

    public int getRelativeX(GameObject object) {
        if(object.getComponentsByClass(Canvas.class).size() > 0) {
            return 0;
        }
        if(!panel.equals(object.getPanel())) {
            return 0;
        }
/*        if(!panel.equals(panel.getWindow().getPanel())) {
            return -panel.getX();
        }*/
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getRelativeY(GameObject object) {
        if(object.getComponentsByClass(Canvas.class).size() > 0) {
            return 0;
        }
        if(!panel.equals(object.getPanel())) {
            return 0;
        }
/*        if(!panel.equals(panel.getWindow().getPanel())) {
            return -panel.getY()
        }*/
        return this.y;
    }

    public float getZoom() {
        return this.zoom;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setBounds(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

}
