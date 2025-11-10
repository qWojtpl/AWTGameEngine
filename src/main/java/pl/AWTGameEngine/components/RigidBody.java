package pl.AWTGameEngine.components;

import physx.common.PxIDENTITYEnum;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.geometry.PxBoxGeometry;
import physx.physics.*;
import pl.AWTGameEngine.annotations.*;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.RenderEngine;
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
    private final static double z2d = 2;

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
        if(boxGeometry == null) {
            return;
        }
        PxVec3 newVector = new PxVec3((float) size.getX() / 2, (float) size.getY() / 2, (float) size.getZ() / 2);
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
    public static class Dynamic extends RigidBody {

        protected PxRigidDynamic rigidDynamic;

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

        @SerializationSetter
        public void setDisableGravity(String disableGravity) {
            setDisableGravity(Boolean.parseBoolean(disableGravity));
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

    @DefaultComponent
    @WebComponent
    public static class Dynamic2D extends Dynamic {

        public Dynamic2D(GameObject object) {
            super(object);
        }

        @Override
        public void initialize() {
            getObject().setSize(new TransformSet(getObject().getSizeX(), getObject().getSizeY(), z2d));
            super.initialize();
        }

        @Override
        public void physicsUpdate() {
            PxVec3 vec3 = rigidDynamic.getGlobalPose().getP();
            PxVec3 position2d = new PxVec3(vec3.getX(), -vec3.getY(), 0);
            PxQuat quaternion = rigidDynamic.getGlobalPose().getQ();
            PxQuat rotation2d = new PxQuat(0, 0, 0, 0);
            updateCachedPositions(position2d, rotation2d);
            position2d.destroy();
            rotation2d.destroy();
        }

    }

    @ComponentFX
    @ComponentGL
    public static class Static extends RigidBody {

        protected PxRigidStatic rigidStatic;

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

    @DefaultComponent
    @WebComponent
    public static class Static2D extends Static {

        public Static2D(GameObject object) {
            super(object);
        }

        @Override
        public void initialize() {
            getObject().setSize(new TransformSet(getObject().getSizeX(), getObject().getSizeY(), z2d));
            super.initialize();
        }

        @Override
        public void physicsUpdate() {
            PxVec3 vec3 = rigidStatic.getGlobalPose().getP();
            PxVec3 position2d = new PxVec3(vec3.getX(), -vec3.getY(), 0);
            PxQuat quaternion = rigidStatic.getGlobalPose().getQ();
            PxQuat rotation2d = new PxQuat(quaternion.getX(), 0, 0, quaternion.getW());
            updateCachedPositions(position2d, rotation2d);
            position2d.destroy();
            rotation2d.destroy();
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

    //2D
    @Override
    public boolean onUpdatePosition(double newX, double newY) {
        if(!getRenderEngine().equals(RenderEngine.WEB) && !getRenderEngine().equals(RenderEngine.DEFAULT)) {
            return true;
        }
        updatePosition(new TransformSet(newX, -newY, 0));
        return true;
    }

    @Override
    public boolean onUpdateSize(double newX, double newY, double newZ) {
        updateSize(new TransformSet(newX, newY, newZ));
        return true;
    }

    //2D
    @Override
    public boolean onUpdateSize(double newX, double newY) {
        if(!getRenderEngine().equals(RenderEngine.WEB) && !getRenderEngine().equals(RenderEngine.DEFAULT)) {
            return true;
        }
        updateSize(new TransformSet(newX, newY, z2d));
        return true;
    }

    @Override
    public boolean onUpdateRotation(double newX, double newY, double newZ) {
        //todo
        return true;
    }

}