package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.annotations.ComponentFX;
import pl.AWTGameEngine.annotations.ComponentGL;
import pl.AWTGameEngine.annotations.FromXML;
import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.components.RigidBody;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.helpers.RotationHelper;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.TransformSet;

import java.awt.*;

@ComponentFX
@ComponentGL
@Unique
public class CameraFollow extends ObjectComponent {

    private double radius = 200;
    private double verticalAngle = 30;
    private double horizontalAngle = 0;

    public CameraFollow(GameObject object) {
        super(object);
    }

    private void updateCamera() {
        TransformSet position = getObject().getPosition();
        double[] camPos = RotationHelper.radiusLook(position.getX(), position.getY(), position.getZ(), radius, verticalAngle, horizontalAngle);
        getCamera().setPosition(new TransformSet(camPos[0], camPos[1], camPos[2]));
        double[] look = RotationHelper.lookAt(getCamera().getX(), getCamera().getY(), getCamera().getZ(), position.getX(), position.getY(), position.getZ());
        getCamera().setRotation(new TransformSet(look[0], look[1], look[2]));
    }

    @Override
    public void onPhysicsUpdate() {
        if(getKeyListener().hasPressedKey(83)) {
            RigidBody.Dynamic dynamic = (RigidBody.Dynamic) getObject().getComponentByClass(RigidBody.Dynamic.class);
            Camera cam = getCamera();
            double[] look = RotationHelper.rotationToVectorLookAt(
                    cam.getX(), cam.getY(), cam.getZ(),
                    cam.getRotation().getX(),
                    cam.getRotation().getY(),
                    cam.getRotation().getZ()
            );

            double forceX = look[0] - cam.getX();
            double forceY = look[1] - cam.getY();
            double forceZ = look[2] - cam.getZ();

            double len = Math.sqrt(forceX*forceX + forceY*forceY + forceZ*forceZ);
            forceX /= len;
            forceY /= len;
            forceZ /= len;
            dynamic.addForce(new TransformSet(forceX, 0, forceZ), 10);
        }
    }

    @Override
    public void onUpdate() {
        getWindow().moveMouseToCenter();
    }

    @Override
    public boolean onUpdatePosition(double newX, double newY, double newZ) {
        updateCamera();
        return true;
    }

    public double getRadius() {
        return this.radius;
    }

    public double getHorizontalAngle() {
        return this.horizontalAngle;
    }

    public double getVerticalAngle() {
        return this.verticalAngle;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @FromXML
    public void setRadius(String radius) {
        setRadius(Integer.parseInt(radius));
    }

    public void setHorizontalAngle(double angle) {
        this.horizontalAngle = angle;
    }

    @FromXML
    public void setHorizontalAngle(String angle) {
        setHorizontalAngle(Integer.parseInt(angle));
    }

    public void setVerticalAngle(double angle) {
        this.verticalAngle = angle;
    }

    @FromXML
    public void setVerticalAngle(String angle) {
        setVerticalAngle(Integer.parseInt(angle));
    }

}
