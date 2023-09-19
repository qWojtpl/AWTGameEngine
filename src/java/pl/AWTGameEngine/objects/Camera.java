package pl.AWTGameEngine.objects;

public class Camera {

    private static int x = 0;
    private static int y = 0;

    public static int getX() {
        return x;
    }

    public static int getY() {
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

}
