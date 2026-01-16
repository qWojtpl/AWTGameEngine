package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.annotations.*;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.TransformSet;

import java.awt.*;

@ComponentFX
@ComponentMeta(
        name = "MovementFX",
        description = "Basic player movement implementation",
        author = "Wojt_pl"
)
public class MovementFX extends ObjectComponent {

    private boolean noclip = true;
    private double speed = 10;

    public MovementFX(GameObject object) {
        super(object);
    }

    @Override
    public void onUpdate() {

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

        TransformSet rotation = getObject().getRotation();

        double pitchRad = Math.toRadians(rotation.getX());
        double yawRad = Math.toRadians(rotation.getY());

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

        TransformSet position = getObject().getPosition();

        getObject().setPosition(new TransformSet(
                position.getX() + dx,
                position.getY() + dy,
                position.getZ() + dz)
        );
    }

    private void handleRotation() {
        int mouseX = getMouseListener().getMouseScreenX();
        int mouseY = getMouseListener().getMouseScreenY();
        double[] screenCenter = getWindow().getScreenCenter();
        int delta = (int) (screenCenter[0] - mouseX);
        if(delta != 0) {
            moveMouse();
        }
        double newRotationY = getObject().getRotation().getY() + delta * -1;
        newRotationY = newRotationY % 360;

        delta = (int) (screenCenter[1] - mouseY);
        if(delta != 0) {
            moveMouse();
        }
        double newRotationX = getObject().getRotation().getX() + delta;
        if(newRotationX > 90) {
            newRotationX = 90;
        } else if(newRotationX < -90) {
            newRotationX = -90;
        }

        getObject().setRotation(new TransformSet(newRotationX, newRotationY, 0));
    }

    private void moveMouse() {
        getWindow().moveMouseToCenter();
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

    @FromXML
    public void setNoclip(String noclip) {
        setNoclip(Boolean.parseBoolean(noclip));
    }

    @FromXML
    public void setSpeed(String speed) {
        setSpeed(Double.parseDouble(speed));
    }

}
