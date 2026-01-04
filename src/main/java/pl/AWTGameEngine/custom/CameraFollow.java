package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.annotations.ComponentFX;
import pl.AWTGameEngine.annotations.ComponentGL;
import pl.AWTGameEngine.annotations.FromXML;
import pl.AWTGameEngine.components.RigidBody;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.helpers.RotationHelper;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.TransformSet;

@ComponentFX
@ComponentGL
public class CameraFollow extends ObjectComponent {

    private GameObject followObject;

    public CameraFollow(GameObject object) {
        super(object);
    }

    @Override
    public void onPhysicsUpdate() {
        if(followObject == null) {
            return;
        }
        TransformSet position = followObject.getPosition();
        double[] look = RotationHelper.lookAt(getCamera().getX(), getCamera().getY(), getCamera().getZ(), position.getX(), position.getY(), position.getZ());
        getCamera().setRotation(new TransformSet(look[0], look[1], look[2]));
        getCamera().setPosition(new TransformSet(position.getX() + 150, position.getY() + 80, position.getZ()));
        if(getKeyListener().hasPressedKey(83)) {
            RigidBody.Dynamic dynamic = (RigidBody.Dynamic) followObject.getComponentByClass(RigidBody.Dynamic.class);
            Camera cam = getCamera();
            look = RotationHelper.rotationToVectorLookAt(
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

    public GameObject getFollowObject() {
        return this.followObject;
    }

    public void setFollowObject(GameObject object) {
        this.followObject = object;
    }

    @FromXML
    public void setFollowObject(String identifier) {
        setFollowObject(getScene().getGameObjectByName(identifier));
    }

}
