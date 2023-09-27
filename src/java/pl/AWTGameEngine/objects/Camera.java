package pl.AWTGameEngine.objects;

import pl.AWTGameEngine.components.Canvas;

public class Camera {

    private static int x = 0;
    private static int y = 0;
    private static float zoom = 1;

    public int getX() {
        return x;
    }

    public int getRelativeX(GameObject object) {
        if(object.getComponentsByClass(Canvas.class).size() > 0) {
            return 0;
        }
        return x;
    }

    public int getY() {
        return y;
    }

    public int getRelativeY(GameObject object) {
        if(object.getComponentsByClass(Canvas.class).size() > 0) {
            return 0;
        }
        return y;
    }

    public void setX(int x) {
        Camera.x = x;
    }

    public void setY(int y) {
        Camera.y = y;
    }

    public void setBounds(int x, int y) {
        Camera.x = x;
        Camera.y = y;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        Camera.zoom = zoom;
    }

}
