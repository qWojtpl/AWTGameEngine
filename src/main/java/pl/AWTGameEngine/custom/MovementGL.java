package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.components.ComponentMeta;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.helpers.MovementHelper;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.transform.TransformSet;
import pl.AWTGameEngine.windows.Window;

@ComponentGL
@ComponentMeta(
        name = "MovementGL",
        description = "Basic player movement implementation",
        author = "Wojt_pl"
)
public class MovementGL extends ObjectComponent {

    private boolean noclip = true;
    private double speed = 10;
    private boolean focused = true;

    public MovementGL(GameObject object) {
        super(object);
    }

    @Override
    public void onUpdate() {

        if(!(getWindow() instanceof Window)) {
            return;
        }

        if(getKeyListener().hasPressedKey(27)) { // ESC
            focused = !focused;
        }

        if(!focused) {
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
        MovementHelper.handleRotation((Window) getWindow(), getMouseListener(), getCamera(), getObject());
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

    private void moveMouse() {
        getWindow().moveMouseToCenter();
    }

    public boolean isNoclip() {
        return this.noclip;
    }

    public double getSpeed() {
        return this.speed;
    }

    @FromXML
    public void setNoclip(boolean noclip) {
        this.noclip = noclip;
    }

    @FromXML
    public void setSpeed(double speed) {
        this.speed = speed;
    }

}
