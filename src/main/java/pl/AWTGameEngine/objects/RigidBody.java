package pl.AWTGameEngine.objects;

import physx.common.PxIDENTITYEnum;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.geometry.PxBoxGeometry;
import physx.physics.*;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.PhysXManager;

public abstract class RigidBody {

    private final ObjectComponent component;
    protected final PhysXManager physXManager = PhysXManager.getInstance();
    protected PxMaterial material;
    protected PxBoxGeometry boxGeometry;
    protected PxShape shape;
    protected PxPhysics physics;
    protected final PxTransform pose = new PxTransform(PxIDENTITYEnum.PxIdentity);
    protected final PxFilterData filterData = new PxFilterData(1, 1, 0, 0);

    protected double mass = 0.03;

    private QuaternionTransformSet cachedRotation = new QuaternionTransformSet(0, 0, 0, 0);

    public RigidBody(ObjectComponent component) {
        this.component = component;
    }

    public void initialize() {
        physics = physXManager.getPxPhysics();
        boxGeometry = new PxBoxGeometry(
                (float) getComponent().getObject().getSize().getX(),
                (float) getComponent().getObject().getSize().getY(),
                (float) getComponent().getObject().getSize().getZ()
        );
        material = physics.createMaterial(0.5f, 0.5f, 0.5f);
        shape = physics.createShape(boxGeometry, material, true, physXManager.getShapeFlags());
        shape.setSimulationFilterData(filterData);
        updatePosition(getComponent().getObject().getPosition());
    }

    public abstract void destroy();

    protected void releaseObjects() {
        pose.destroy();
        filterData.destroy();
        shape.release();
        boxGeometry.destroy();
        material.release();
    }

    public ObjectComponent getComponent() {
        return this.component;
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
        PxVec3 newVector = new PxVec3((float) size.getX() / 2, (float) size.getY() / 2, (float) size.getZ() / 2);
        boxGeometry.setHalfExtents(newVector);
        newVector.destroy();
    }

    public abstract void physicsUpdate();

    protected void updateCachedPositions(PxVec3 position, PxQuat rotation) {
        GameObject object = component.getObject();
        if(position.getX() != object.getPosition().getX() || position.getY() != object.getPosition().getY() || position.getZ() != object.getPosition().getZ()) {
            object.setPosition(new TransformSet(
                    position.getX(),
                    position.getY(),
                    position.getZ()
            ));
        }
        if(rotation.getX() != cachedRotation.getX() || rotation.getY() != cachedRotation.getY() ||
                rotation.getZ() != cachedRotation.getZ() || rotation.getW() != cachedRotation.getW()) {
            cachedRotation = new QuaternionTransformSet(rotation.getX(), rotation.getY(), rotation.getZ(), rotation.getW());
            object.setQuaternionRotation(new QuaternionTransformSet(rotation.getX(), rotation.getY(), rotation.getZ(), rotation.getW()));
        }
    }

    public static class Dynamic extends RigidBody {

        private PxRigidDynamic rigidDynamic;

        public Dynamic(ObjectComponent component) {
            super(component);
        }

        @Override
        public void initialize() {
            super.initialize();
            rigidDynamic = physics.createRigidDynamic(pose);
            rigidDynamic.attachShape(shape);
            rigidDynamic.setMass((float) mass);
            physXManager.getPxScene().addActor(rigidDynamic);
        }

        @Override
        public void destroy() {
            physXManager.getPxScene().removeActor(rigidDynamic);
            rigidDynamic.release();
        }

        @Override
        public void physicsUpdate() {
            updateCachedPositions(rigidDynamic.getGlobalPose().getP(), rigidDynamic.getGlobalPose().getQ());
        }

    }

    public static class Static extends RigidBody {

        private PxRigidStatic rigidStatic;

        public Static(ObjectComponent component) {
            super(component);
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
            physXManager.getPxScene().removeActor(rigidStatic);
            rigidStatic.release();
        }

        @Override
        public void physicsUpdate() {
            updateCachedPositions(rigidStatic.getGlobalPose().getP(), rigidStatic.getGlobalPose().getQ());
        }

    }

}