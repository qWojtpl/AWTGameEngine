package pl.AWTGameEngine.custom;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import pl.AWTGameEngine.annotations.Component3D;
import pl.AWTGameEngine.annotations.ComponentMeta;
import pl.AWTGameEngine.annotations.DefaultComponent;
import pl.AWTGameEngine.annotations.WebComponent;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.panels.Panel3D;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

@Component3D
@DefaultComponent
@WebComponent
@ComponentMeta(
        name = "Movement3D",
        description = "Basic player movement implementation",
        author = "Wojt_pl"
)
public class Movement3D extends ObjectComponent {

    private final int CENTER_X;
    private final int CENTER_Y;
    private Robot robot;

    public Movement3D(GameObject object) {
        super(object);
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        try {
            this.robot = new Robot(device);
        } catch(Exception e) {
            Logger.log("Error initializing Movement3D component", e);
        }
        Rectangle bounds = device.getConfigurations()[0].getBounds();
        CENTER_X = (int) (bounds.getWidth() / 2);
        CENTER_Y = (int) (bounds.getHeight() / 2);
        moveMouse();
    }

    @Override
    public void onUpdate() {
        if(!getWindow().isFocused()) {
            return;
        }
        int speed = 10;

        double forward = 0, right = 0, up = 0;

        if(getKeyListener().hasPressedKey(87)) { // W
            forward = speed;
        }
        if(getKeyListener().hasPressedKey(65)) { // A
            right = -speed;
        }
        if(getKeyListener().hasPressedKey(83)) { // S
            forward = -speed;
        }
        if(getKeyListener().hasPressedKey(68)) { // D
            right = speed;
        }

        handleMovement(forward, right, up, false);
        handleRotation();
        moveMouse();
    }

    private void handleMovement(double forward, double right, double up, boolean noclip) {
        Panel3D panel = (Panel3D) getObject().getPanel();

        Group pitchGroup = panel.getCameraPitch();
        Group yawGroup = panel.getCameraYaw();

        pitchGroup.setRotationAxis(Rotate.X_AXIS);
        yawGroup.setRotationAxis(Rotate.Y_AXIS);

        double pitchRad = Math.toRadians(pitchGroup.getRotate());
        double yawRad = Math.toRadians(yawGroup.getRotate());

        // Forward
        double dirX = Math.sin(yawRad) * Math.cos(pitchRad);
        double dirY = -Math.sin(pitchRad);
        double dirZ = Math.cos(yawRad) * Math.cos(pitchRad);

        // Left/right
        double rightX = Math.cos(yawRad);
        double rightZ = -Math.sin(yawRad);

        double dx = dirX * forward + rightX * right;
        double dy = dirY * forward + up;
        double dz = dirZ * forward + rightZ * right;

        if(!noclip) {
            dy = 0;
        }

        getCamera().setX((int) (pitchGroup.getTranslateX() + dx));
        getCamera().setY((int) (pitchGroup.getTranslateY() + dy));
        getCamera().setZ((int) (pitchGroup.getTranslateZ() + dz));
    }

    private void handleRotation() {
        int mouseX = getMouseListener().getMouseScreenX();
        int delta = CENTER_X - mouseX;
        int newRotationY = getCamera().getRotation().getY() + delta * -1;
        newRotationY = newRotationY % 360;
        getCamera().setRotationY(newRotationY);

        int mouseY = getMouseListener().getMouseScreenY();
        delta = CENTER_Y - mouseY;
        int newRotationX = getCamera().getRotation().getX() + delta;
        if(newRotationX > 90) {
            newRotationX = 90;
        } else if(newRotationX < -90) {
            newRotationX = -90;
        }
        getCamera().setRotationX(newRotationX);
    }

    private void moveMouse() {
        robot.mouseMove(CENTER_X, CENTER_Y);
    }

}
