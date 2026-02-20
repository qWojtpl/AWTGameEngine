package pl.AWTGameEngine.components;

import physx.common.PxIDENTITYEnum;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.geometry.PxBoxGeometry;
import physx.geometry.PxGeometry;
import physx.physics.*;
import pl.AWTGameEngine.annotations.components.management.Conflicts;
import pl.AWTGameEngine.annotations.components.management.ConflictsWith;
import pl.AWTGameEngine.annotations.components.management.Unique;
import pl.AWTGameEngine.annotations.components.types.ComponentFX;
import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.QuaternionTransformSet;
import pl.AWTGameEngine.objects.TransformSet;

public abstract class RigidBody extends ObjectComponent {

    // PhysX
    protected final PhysXManager physXManager = PhysXManager.getInstance();
    protected PxMaterial material;
    protected PxGeometry geometry;
    protected PxShape shape;
    protected PxPhysics physics;
    protected final PxTransform pose = new PxTransform(PxIDENTITYEnum.PxIdentity);
    protected final PxFilterData filterData = new PxFilterData(1, -1, PxPairFlagEnum.eNOTIFY_TOUCH_FOUND.value | PxPairFlagEnum.eNOTIFY_TOUCH_LOST.value | PxPairFlagEnum.eNOTIFY_CONTACT_POINTS.value, 0);

    // Internal variables
    protected double mass = 0.03;
    private QuaternionTransformSet cachedRotation = new QuaternionTransformSet(0, 0, 0, 0);

    public RigidBody(GameObject object) {
        super(object);
    }

    protected void initialize() {
        physics = physXManager.getPxPhysics();
        createGeometry();
//        material = physics.createMaterial(0.5f, 0.5f, 0.5f);
        material = physXManager.getDefaultMaterial();
        shape = physics.createShape(geometry, material, true, physXManager.getShapeFlags());
        shape.setSimulationFilterData(filterData);
        updatePosition(getObject().getPosition());
    }

    public abstract void destroy();

    protected void releaseObjects() {
        pose.destroy();
        filterData.destroy();
        shape.release();
        geometry.destroy();
        material.release();
    }

    public PxRigidActor getPxActor() {
        return this.shape.getActor();
    }

    public double getMass() {
        return this.mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    protected void createGeometry() {
        geometry = new PxBoxGeometry(
                (float) getObject().getSize().getX(),
                (float) getObject().getSize().getY(),
                (float) getObject().getSize().getZ()
        );
    }

    public void updatePosition(TransformSet position) {
        PxVec3 newVector = new PxVec3((float) position.getX(), (float) position.getY(), (float) position.getZ());
        pose.setP(newVector);
        newVector.destroy();
    }

    public void updateSize(TransformSet size) {
//        PxVec3 newVector = new PxVec3((float) size.getX(), (float) size.getY(), (float) size.getZ());
//        boxGeometry.setHalfExtents(newVector);
//        newVector.destroy();
    }

    public void updateRotation() {
        PxQuat newQuat = new PxQuat(
                (float) getObject().getQuaternionRotation().getX(),
                (float) getObject().getQuaternionRotation().getY(),
                (float) getObject().getQuaternionRotation().getZ(),
                (float) getObject().getQuaternionRotation().getW()
        );
        pose.setQ(newQuat);
        newQuat.destroy();
    }

    public abstract void physicsUpdate();

    protected void updateCachedPositions(PxVec3 position, PxQuat rotation) {
        if(position.getX() != getObject().getPosition().getX() || position.getY() != getObject().getPosition().getY() || position.getZ() != getObject().getPosition().getZ()) {
            getObject().setPosition(TransformSet.fromPhysX(position)/*, this*/);
        }
        if(rotation.getX() != cachedRotation.getX() || rotation.getY() != cachedRotation.getY() ||
                rotation.getZ() != cachedRotation.getZ() || rotation.getW() != cachedRotation.getW()) {
            cachedRotation = QuaternionTransformSet.fromPhysX(rotation);
            getObject().setQuaternionRotation(QuaternionTransformSet.fromPhysX(rotation), this);
        }
    }

    @ComponentFX
    @ComponentGL
    @Unique
    @Conflicts({
            @ConflictsWith(RigidBody.Static.class),
            @ConflictsWith(RigidBody.Kinematic.class)
    })
    public static class Dynamic extends RigidBody {

        protected PxRigidDynamic rigidDynamic;

        private boolean disableGravity = false;
        private TransformSet savedLinearVelocity = null;
        private TransformSet savedAngularVelocity = null;

        public Dynamic(GameObject object) {
            super(object);
        }

        @Override
        public void initialize() {
            super.initialize();
            rigidDynamic = physics.createRigidDynamic(pose);
            rigidDynamic.setName(getObject().getIdentifier());
            rigidDynamic.attachShape(shape);
            rigidDynamic.setMass((float) mass);
            //
            rigidDynamic.setSleepThreshold(0.05f);
            rigidDynamic.setWakeCounter(0.1f);
            rigidDynamic.setSolverIterationCounts(20, 8);
            //
            setDisableGravity(disableGravity);
            //
            if(savedLinearVelocity != null) {
                setLinearVelocity(savedLinearVelocity);
                savedLinearVelocity = null;
            }
            if(savedAngularVelocity != null) {
                setAngularVelocity(savedAngularVelocity);
                savedAngularVelocity = null;
            }
            physXManager.getPxScene(getScene()).addActor(rigidDynamic);
        }

        @Override
        public void destroy() {
            rigidDynamic.detachShape(shape);
            releaseObjects();
            physXManager.getPxScene(getScene()).removeActor(rigidDynamic);
            rigidDynamic.release();
        }

        @Override
        public void physicsUpdate() {
            updateCachedPositions(rigidDynamic.getGlobalPose().getP(), rigidDynamic.getGlobalPose().getQ());
        }

        public void addForce(TransformSet vector, float force) {
            PxVec3 vec3 = new PxVec3((float) vector.getX() * force, (float) vector.getY() * force, (float) vector.getZ() * force);
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

        @SaveState(name = "linearVelocity")
        public TransformSet getLinearVelocity() {
            return TransformSet.fromPhysX(rigidDynamic.getLinearVelocity());
        }

        public void setLinearVelocity(TransformSet linearVelocity) {
            if(rigidDynamic == null) {
                this.savedLinearVelocity = linearVelocity;
                return;
            }
            PxVec3 vec3 = new PxVec3((float) linearVelocity.getX(), (float) linearVelocity.getY(), (float) linearVelocity.getZ());
            rigidDynamic.setLinearVelocity(vec3);
            vec3.destroy();
        }

        @FromXML
        public void setLinearVelocity(String linearVelocity) {
            setLinearVelocity(new TransformSet().deserialize(linearVelocity));
        }

        @SaveState(name = "angularVelocity")
        public TransformSet getAngularVelocity() {
            return TransformSet.fromPhysX(rigidDynamic.getAngularVelocity());
        }

        public void setAngularVelocity(TransformSet angularVelocity) {
            if(rigidDynamic == null) {
                this.savedAngularVelocity = angularVelocity;
                return;
            }
            PxVec3 vec3 = new PxVec3((float) angularVelocity.getX(), (float) angularVelocity.getY(), (float) angularVelocity.getZ());
            rigidDynamic.setAngularVelocity(vec3);
            vec3.destroy();
        }

        @FromXML
        public void setAngularVelocity(String angularVelocity) {
            setAngularVelocity(new TransformSet().deserialize(angularVelocity));
        }

    }

    @ComponentFX
    @ComponentGL
    @Unique
    @Conflicts({
            @ConflictsWith(RigidBody.Dynamic.class),
            @ConflictsWith(RigidBody.Kinematic.class)
    })
    public static class Static extends RigidBody {

        protected PxRigidStatic rigidStatic;

        public Static(GameObject object) {
            super(object);
        }

        @Override
        public void initialize() {
            super.initialize();
            rigidStatic = physics.createRigidStatic(pose);
            rigidStatic.setName(getObject().getIdentifier());
            rigidStatic.attachShape(shape);
            physXManager.getPxScene(getScene()).addActor(rigidStatic);
        }

        @Override
        public void destroy() {
            rigidStatic.detachShape(shape);
            releaseObjects();
            physXManager.getPxScene(getScene()).removeActor(rigidStatic);
            rigidStatic.release();
        }

        @Override
        public void physicsUpdate() {
            updateCachedPositions(rigidStatic.getGlobalPose().getP(), rigidStatic.getGlobalPose().getQ());
        }

    }

    @ComponentFX
    @ComponentGL
    @Unique
    @Conflicts({
            @ConflictsWith(RigidBody.Dynamic.class),
            @ConflictsWith(RigidBody.Static.class)
    })
    public static class Kinematic extends RigidBody.Dynamic {

        public Kinematic(GameObject object) {
            super(object);
        }

        @Override
        public void initialize() {
            super.initialize();
            rigidDynamic.setRigidBodyFlag(PxRigidBodyFlagEnum.eKINEMATIC, true);
        }

        @Override
        public void physicsUpdate() {
            PxVec3 vec3 = new PxVec3(
                    (float) getObject().getPosition().getX(),
                    (float) getObject().getPosition().getY(),
                    (float) getObject().getPosition().getZ()
            );
            PxQuat quat = new PxQuat(
                    (float) getObject().getQuaternionRotation().getX(),
                    (float) getObject().getQuaternionRotation().getY(),
                    (float) getObject().getQuaternionRotation().getZ(),
                    (float) getObject().getQuaternionRotation().getW()
            );
            PxTransform transform = new PxTransform(vec3, quat);
            rigidDynamic.setKinematicTarget(transform);
            vec3.destroy();
            quat.destroy();
            transform.destroy();
        }

        @Override
        public void addForce(TransformSet vector, float force) {
            throw new RuntimeException("Body must be non-kinematic. Kinematic add force may be implemented later.");
        }

    }

    public abstract static class TopDown {

        @DefaultComponent
        @WebComponent
        @ComponentGL
        public static class Dynamic extends RigidBody.Dynamic {

            public Dynamic(GameObject object) {
                super(object);
            }

            @Override
            public void initialize() {
                super.initialize();
                PxVec3 vector = new PxVec3(0, 0, 0);
                rigidDynamic.setMassSpaceInertiaTensor(vector); // disable rotation
                vector.destroy();
            }

            @Override
            public void createGeometry() {
                geometry = new PxBoxGeometry(
                        (float) getObject().getSize().getX() / 2,
                        5,
                        (float) getObject().getSize().getY() / 2
                );
            }

            @Override
            public void physicsUpdate() {
                PxVec3 vec3 = rigidDynamic.getGlobalPose().getP();
                PxVec3 newVec = new PxVec3(vec3.getX(), vec3.getZ(), 5);
                updateCachedPositions(newVec, rigidDynamic.getGlobalPose().getQ());
                newVec.destroy();
            }

            @Override
            public void updatePosition(TransformSet position) {
                PxVec3 vec3 = new PxVec3((float) position.getX(), (float) position.getZ(), 5);
                pose.setP(vec3);
                vec3.destroy();
            }

            @Override
            public void addForce(TransformSet vector, float force) {
                super.addForce(new TransformSet(vector.getX(), vector.getZ(), vector.getY()), force);
            }

        }

        @DefaultComponent
        @WebComponent
        @ComponentGL
        public static class Static extends RigidBody.Static {

            private int layer = 0;

            public Static(GameObject object) {
                super(object);
            }

            @Override
            public void createGeometry() {
                geometry = new PxBoxGeometry(
                        (float) getObject().getSize().getX() / 2,
                        (float) 5 / 2,
                        (float) getObject().getSize().getY() / 2
                );
            }

            @Override
            public void updatePosition(TransformSet position) {
                PxVec3 vec3 = new PxVec3((float) position.getX(), layer * 5, (float) position.getZ());
                pose.setP(vec3);
                vec3.destroy();
            }

            @SaveState(name = "layer")
            public int getLayer() {
                return this.layer;
            }

            public void setLayer(int layer) {
                this.layer = layer;
            }

            @FromXML
            public void setLayer(String layer) {
                setLayer(Integer.parseInt(layer));
            }

        }

        @DefaultComponent
        @WebComponent
        @ComponentGL
        public static class Kinematic extends RigidBody.Kinematic {

            public Kinematic(GameObject object) {
                super(object);
            }

            @Override
            public void physicsUpdate() {
                PxVec3 vec3 = new PxVec3(
                        (float) getObject().getPosition().getX(),
                        (float) getObject().getPosition().getZ(),
                        (float) getObject().getPosition().getY()
                );
                PxQuat rotation = new PxQuat(0, 0, 0, 0);
                PxTransform transform = new PxTransform(vec3, rotation);
                rigidDynamic.setKinematicTarget(transform);
                vec3.destroy();
                rotation.destroy();
                transform.destroy();
            }

            @Override
            public void updatePosition(TransformSet position) {
                PxVec3 vec3 = new PxVec3((float) position.getX(), (float) position.getZ(), (float) position.getY());
                pose.setP(vec3);
                vec3.destroy();
            }

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
    public void onUpdateRotation() {
        updateRotation();
    }

}