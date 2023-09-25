package pl.AWTGameEngine.objects;

import pl.AWTGameEngine.components.Canvas;

public class Camera {

    private static int x = 0;
    private static int y = 0;
    private static float zoom = 1;

    public static int getX() {
        return x;
    }

    public static int getRelativeX(GameObject object) {
        if(object.getComponentsByClass(Canvas.class).size() > 0) {
            return 0;
        }
        return x;
    }

    public static int getY() {
        return y;
    }

    public static int getRelativeY(GameObject object) {
        if(object.getComponentsByClass(Canvas.class).size() > 0) {
            return 0;
        }
        return y;
    }

    public static void setX(int x) {
        Camera.x = x;
    }

    public static void setY(int y) {
        Camera.y = y;
    }

    public static void setBounds(int x, int y) {
        Camera.x = x;
        Camera.y = y;
    }

    public static float getZoom() {
        return zoom;
    }

    public static void setZoom(float zoom) {
        Camera.zoom = zoom;
    }

}
