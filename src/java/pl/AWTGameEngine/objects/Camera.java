package pl.AWTGameEngine.objects;

import pl.AWTGameEngine.components.Canvas;

public class Camera {

    private int x = 0;
    private int y = 0;
    private float zoom = 1;

    public int getX() {
        return this.x;
    }

    public int getRelativeX(GameObject object) {
        if(object.getComponentsByClass(Canvas.class).size() > 0) {
            return 0;
        }
        if(!object.getScene().getWindow().getPanel().equals(object.getPanel())) {
            return 0;
        }
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getRelativeY(GameObject object) {
        if(object.getComponentsByClass(Canvas.class).size() > 0) {
            return 0;
        }
        if(!object.getScene().getWindow().getPanel().equals(object.getPanel())) {
            return 0;
        }
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
