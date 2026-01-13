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

@ComponentFX
@ComponentGL
@Unique
public class CameraFollow extends ObjectComponent {

    public CameraFollow(GameObject object) {
        super(object);
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
    public boolean onUpdatePosition(double newX, double newY, double newZ) {
        TransformSet position = getObject().getPosition();
        getCamera().setPosition(new TransformSet(position.getX() + 200, position.getY() + 80, position.getZ()));
        double[] look = RotationHelper.lookAt(getCamera().getX(), getCamera().getY(), getCamera().getZ(), position.getX(), position.getY(), position.getZ());
        getCamera().setRotation(new TransformSet(look[0], look[1], look[2]));
        return true;
    }

}
