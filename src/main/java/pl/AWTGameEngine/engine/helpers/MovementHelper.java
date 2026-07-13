package pl.AWTGameEngine.engine.helpers;

import pl.AWTGameEngine.engine.listeners.MouseListener;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.render.Camera;
import pl.AWTGameEngine.objects.transform.TransformSet;
import pl.AWTGameEngine.windows.Window;

public class MovementHelper {

    public static void handleRotation(Window window, MouseListener mouseListener, Camera camera, GameObject object) {
        int mouseX = mouseListener.getMouseScreenX();
        int mouseY = mouseListener.getMouseScreenY();

        double[] screenCenter = window.getScreenCenter();

        int delta = (int) (screenCenter[0] - mouseX);
        if(delta != 0) {
            moveMouse(window);
        }
        double newRotationY = camera.getRotation().getY() + delta * -1;
        newRotationY = newRotationY % 360;

        delta = (int) (screenCenter[1] - mouseY);
        if(delta != 0) {
            moveMouse(window);
        }
        double newRotationX = camera.getRotation().getX() + delta;
        if(newRotationX > 90) {
            newRotationX = 90;
        } else if(newRotationX < -90) {
            newRotationX = -90;
        }

        TransformSet rotation = new TransformSet(newRotationX, newRotationY, 30);
        camera.setRotation(rotation);
        object.setRotation(rotation);
    }

    private static void moveMouse(Window window) {
        window.moveMouseToCenter();
    }

}
