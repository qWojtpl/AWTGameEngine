package pl.AWTGameEngine.components;

import physx.PxTopLevelFunctions;
import physx.extensions.PxDistanceJoint;
import physx.extensions.PxDistanceJointFlagEnum;
import pl.AWTGameEngine.annotations.*;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.objects.GameObject;

public abstract class Joint extends ObjectComponent {

    protected final PhysXManager physXManager = PhysXManager.getInstance();
    protected RigidBody firstBody;
    protected RigidBody secondBody;

    public Joint(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        firstBody = (RigidBody) getObject().getComponentByClass(RigidBody.class);
        if(secondBody != null) {
            createJoint();
        }
    }

    @Override
    public void onRemoveComponent() {

    }

    public abstract void createJoint();

    public RigidBody getReference() {
        return this.secondBody;
    }

    public void setReference(RigidBody secondBody) {
        this.secondBody = secondBody;
    }

    @FromXML
    protected void setReference(String identifier) {
        setReference((RigidBody) getScene().getGameObjectByName(identifier).getComponentByClass(RigidBody.class));
    }

    @ComponentGL
    @ComponentFX
    public static class Distance extends Joint {

        private PxDistanceJoint joint;

        public Distance(GameObject object) {
            super(object);
        }

        @Override
        public void createJoint() {
            joint = PxTopLevelFunctions.DistanceJointCreate(
                    physXManager.getPxPhysics(),
                    firstBody.getPxActor(),
                    firstBody.getPxActor().getGlobalPose(),
                    secondBody.getPxActor(),
                    secondBody.getPxActor().getGlobalPose()
            );
            joint.setMinDistance(10.0f);
            joint.setDistanceJointFlag(PxDistanceJointFlagEnum.eMIN_DISTANCE_ENABLED, true);
        }

        public PxDistanceJoint getJoint() {
            return this.joint;
        }

        @FromXML
        public void setReference(String identifier) {
            super.setReference(identifier);
        }

    }

}
