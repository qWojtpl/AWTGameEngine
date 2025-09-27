package pl.AWTGameEngine.custom;

import physx.vehicle2.EngineDriveVehicleEnum;
import pl.AWTGameEngine.annotations.ComponentGL;
import pl.AWTGameEngine.annotations.ComponentMeta;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.TransformSet;
import pl.AWTGameEngine.windows.Window;

import java.awt.*;

@ComponentGL
@ComponentMeta(
        name = "MovementGL",
        description = "Basic player movement implementation",
        author = "Wojt_pl"
)
public class MovementGL extends ObjectComponent {

    private final int CENTER_X;
    private final int CENTER_Y;
    private Robot robot;
    private boolean noclip = true;
    private double speed = 10;

    public MovementGL(GameObject object) {
        super(object);
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        try {
            this.robot = new Robot(device);
        } catch(Exception e) {
            Logger.exception("Error initializing MovementGL component", e);
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

        handleMovement(forward, right, up);
        handleRotation();
    }

    private void handleMovement(double forward, double right, double up) {

        TransformSet rotation = getCamera().getRotation();

        double pitchRad = Math.toRadians(rotation.getX());
        double yawRad = Math.toRadians(rotation.getY());

        // Forward
        double dirX = Math.cos(pitchRad) * Math.sin(yawRad);
        double dirY = Math.sin(pitchRad);
        double dirZ = -Math.cos(pitchRad) * Math.cos(yawRad);

        // Left/right
        double rightX = Math.cos(yawRad);
        double rightZ = Math.sin(yawRad);

        double dx = dirX * forward + rightX * right;
        double dy = dirY * forward + up;
        double dz = dirZ * forward + rightZ * right;

        if(!noclip) {
            dy = 0;
        }

        TransformSet position = new TransformSet(getCamera().getX() + dx, getCamera().getY() + dy, getCamera().getZ() + dz);
        getCamera().setPosition(position);
        getObject().setPosition(position);
    }

    private void handleRotation() {
        int mouseX = getMouseListener().getMouseScreenX();
        int mouseY = getMouseListener().getMouseScreenY();

        int delta = CENTER_X - mouseX;
        if(delta != 0) {
            moveMouse();
        }
        double newRotationY = getCamera().getRotation().getY() + delta * -1;
        newRotationY = newRotationY % 360;

        delta = CENTER_Y - mouseY;
        if(delta != 0) {
            moveMouse();
        }
        double newRotationX = getCamera().getRotation().getX() + delta;
        if(newRotationX > 90) {
            newRotationX = 90;
        } else if(newRotationX < -90) {
            newRotationX = -90;
        }

        TransformSet rotation = new TransformSet(newRotationX, newRotationY, 30);
        getCamera().setRotation(rotation);
        getObject().setRotation(rotation);
    }

    private void moveMouse() {
        robot.mouseMove(CENTER_X, CENTER_Y);
    }

    public boolean isNoclip() {
        return this.noclip;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setNoclip(boolean noclip) {
        this.noclip = noclip;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @SerializationSetter
    public void setNoclip(String noclip) {
        setNoclip(Boolean.parseBoolean(noclip));
    }

    @SerializationSetter
    public void setSpeed(String speed) {
        setSpeed(Double.parseDouble(speed));
    }

}
