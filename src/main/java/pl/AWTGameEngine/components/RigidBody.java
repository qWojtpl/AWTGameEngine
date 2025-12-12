package pl.AWTGameEngine.components;

import physx.common.PxIDENTITYEnum;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.geometry.PxBoxGeometry;
import physx.physics.*;
import pl.AWTGameEngine.annotations.ComponentFX;
import pl.AWTGameEngine.annotations.ComponentGL;
import pl.AWTGameEngine.annotations.ConflictsWith;
import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.QuaternionTransformSet;
import pl.AWTGameEngine.objects.TransformSet;

public abstract class RigidBody extends ObjectComponent {

    // PhysX
    protected final PhysXManager physXManager = PhysXManager.getInstance();
    protected PxMaterial material;
    protected PxBoxGeometry boxGeometry;
    protected PxShape shape;
    protected PxPhysics physics;
    protected final PxTransform pose = new PxTransform(PxIDENTITYEnum.PxIdentity);
    protected final PxFilterData filterData = new PxFilterData(1, 1, 0, 0);

    // Internal variables
    protected double mass = 0.03;
    private QuaternionTransformSet cachedRotation = new QuaternionTransformSet(0, 0, 0, 0);

    public RigidBody(GameObject object) {
        super(object);
    }

    public void initialize() {
        physics = physXManager.getPxPhysics();
        boxGeometry = new PxBoxGeometry(
                (float) getObject().getSize().getX(),
                (float) getObject().getSize().getY(),
                (float) getObject().getSize().getZ()
        );
        material = physics.createMaterial(0.5f, 0.5f, 0.5f);
        shape = physics.createShape(boxGeometry, material, true, physXManager.getShapeFlags());
        shape.setSimulationFilterData(filterData);
        updatePosition(getObject().getPosition());
    }

    public abstract void destroy();

    protected void releaseObjects() {
        pose.destroy();
        filterData.destroy();
        shape.release();
        boxGeometry.destroy();
        material.release();
    }

    public double getMass() {
        return this.mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void updatePosition(TransformSet position) {
        PxVec3 newVector = new PxVec3((float) position.getX(), (float) position.getY(), (float) position.getZ());
        pose.setP(newVector);
        newVector.destroy();
    }

    public void updateSize(TransformSet size) {
        PxVec3 newVector = new PxVec3((float) size.getX(), (float) size.getY(), (float) size.getZ());
        boxGeometry.setHalfExtents(newVector);
        newVector.destroy();
    }

    public abstract void physicsUpdate();

    protected void updateCachedPositions(PxVec3 position, PxQuat rotation) {
        if(position.getX() != getObject().getPosition().getX() || position.getY() != getObject().getPosition().getY() || position.getZ() != getObject().getPosition().getZ()) {
            getObject().setPosition(new TransformSet(
                    position.getX(),
                    position.getY(),
                    position.getZ()
            ));
        }
        if(rotation.getX() != cachedRotation.getX() || rotation.getY() != cachedRotation.getY() ||
                rotation.getZ() != cachedRotation.getZ() || rotation.getW() != cachedRotation.getW()) {
            cachedRotation = new QuaternionTransformSet(rotation.getX(), rotation.getY(), rotation.getZ(), rotation.getW());
            getObject().setQuaternionRotation(new QuaternionTransformSet(rotation.getX(), rotation.getY(), rotation.getZ(), rotation.getW()));
        }
    }

    @ComponentFX
    @ComponentGL
    @Unique
    @ConflictsWith(RigidBody.Static.class)
    public static class Dynamic extends RigidBody {

        private PxRigidDynamic rigidDynamic;

        private boolean disableGravity = false;

        public Dynamic(GameObject object) {
            super(object);
        }

        @Override
        public void initialize() {
            super.initialize();
            rigidDynamic = physics.createRigidDynamic(pose);
            rigidDynamic.attachShape(shape);
            rigidDynamic.setMass((float) mass);
            setDisableGravity(disableGravity);
            physXManager.getPxScene().addActor(rigidDynamic);
        }

        @Override
        public void destroy() {
            rigidDynamic.detachShape(shape);
            physXManager.getPxScene().removeActor(rigidDynamic);
            rigidDynamic.release();
        }

        @Override
        public void physicsUpdate() {
            updateCachedPositions(rigidDynamic.getGlobalPose().getP(), rigidDynamic.getGlobalPose().getQ());
        }

        public void addForce(TransformSet force) {
            PxVec3 vec3 = new PxVec3((float) force.getX(), (float) force.getY(), (float) force.getZ());
            rigidDynamic.addForce(vec3);
            vec3.destroy();
        }

        public void setDisableGravity(boolean disableGravity) {
            this.disableGravity = disableGravity;
            if(rigidDynamic != null) {
                rigidDynamic.setActorFlag(PxActorFlagEnum.eDISABLE_GRAVITY, disableGravity);
            }
        }

        public boolean isGravityDisabled() {
            return this.disableGravity;
        }

    }

    @ComponentFX
    @ComponentGL
    @Unique
    @ConflictsWith(RigidBody.Dynamic.class)
    public static class Static extends RigidBody {

        private PxRigidStatic rigidStatic;

        public Static(GameObject object) {
            super(object);
        }

        @Override
        public void initialize() {
            super.initialize();
            rigidStatic = physics.createRigidStatic(pose);
            rigidStatic.attachShape(shape);
            physXManager.getPxScene().addActor(rigidStatic);
        }

        @Override
        public void destroy() {
            rigidStatic.detachShape(shape);
            physXManager.getPxScene().removeActor(rigidStatic);
            rigidStatic.release();
        }

        @Override
        public void physicsUpdate() {
            updateCachedPositions(rigidStatic.getGlobalPose().getP(), rigidStatic.getGlobalPose().getQ());
        }

    }

    // Events

    @Override
    public void onAddComponent() {
        initialize();
    }

    @Override
    public void onRemoveComponent() {
        destroy();
    }

    @Override
    public void onPhysicsUpdate() {
        physicsUpdate();
    }

    @Override
    public boolean onUpdatePosition(double newX, double newY, double newZ) {
        updatePosition(new TransformSet(newX, newY, newZ));
        return true;
    }

    @Override
    public boolean onUpdateSize(double newX, double newY, double newZ) {
        updateSize(new TransformSet(newX, newY, newZ));
        return true;
    }

    @Override
    public boolean onUpdateRotation(double newX, double newY, double newZ) {
        //todo
        return true;
    }

}