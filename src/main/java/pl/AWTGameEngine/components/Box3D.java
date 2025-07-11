package pl.AWTGameEngine.components;

import physx.common.PxIDENTITYEnum;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.geometry.PxBoxGeometry;
import physx.physics.*;
import pl.AWTGameEngine.annotations.*;
import pl.AWTGameEngine.components.base.Base3DShape;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.helpers.RotationHelper;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.graphics.Renderable3D;
import pl.AWTGameEngine.engine.panels.Panel3D;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.TransformSet;

@Component3D
@Unique
@Conflicts({
        @ConflictsWith(Sphere3D.class),
        @ConflictsWith(Cylinder3D.class)
})
public class Box3D extends Base3DShape implements Renderable3D {

    private final PhysXManager physXManager;
    private PxMaterial material;
    private PxBoxGeometry boxGeometry;
    private PxShape shape;
    private final PxTransform pose = new PxTransform(PxIDENTITYEnum.PxIdentity);
    private final PxFilterData filterData = new PxFilterData(1, 1, 0, 0);

    public Box3D(GameObject object) {
        super(object);
        physXManager = ((Panel3D) getPanel()).getPhysXManager();
    }

    @Override
    protected void createShape() {
        GraphicsManager3D g = ((Panel3D) getPanel()).getGraphicsManager3D();

        g.createBox(new GraphicsManager3D.RenderOptions(
                getObject().getIdentifier(),
                getObject().getPosition(),
                getObject().getSize(),
                getObject().getRotation(),
                getSprite(),
                getColor()
        ));
    }

    @Override
    protected void removeShape() {
        ((Panel3D) getPanel()).getGraphicsManager3D().removeBox(getObject().getIdentifier());
    }

    @Override
    public void onAddComponent() {
        createShape();
        PxPhysics physics = physXManager.getPxPhysics();
        boxGeometry = new PxBoxGeometry(
                (float) getObject().getSize().getX(),
                (float) getObject().getSize().getY(),
                (float) getObject().getSize().getZ()
        );
        material = physics.createMaterial(0.5f, 0.5f, 0.5f);
        shape = physics.createShape(boxGeometry, material, true, physXManager.getShapeFlags());
        shape.setSimulationFilterData(filterData);
        updateVectorPosition(getObject().getX(), getObject().getY(), getObject().getZ());
        if(isStaticShape()) {
            rigidStatic = physics.createRigidStatic(pose);
            rigidStatic.attachShape(shape);
            physXManager.getPxScene().addActor(rigidStatic);
        } else {
            rigidDynamic = physics.createRigidDynamic(pose);
            rigidDynamic.attachShape(shape);
            rigidDynamic.setMass((float) getMass());
            physXManager.getPxScene().addActor(rigidDynamic);
        }
    }

    @Override
    public void onRemoveComponent() {
        removeShape();
        if(isStaticShape()) {
            physXManager.getPxScene().removeActor(rigidStatic);
            rigidStatic.release();
        } else {
            physXManager.getPxScene().removeActor(rigidDynamic);
            rigidDynamic.release();
        }
        pose.destroy();
        filterData.destroy();
        shape.release();
        boxGeometry.destroy();
        material.release();
    }

    private void updateVectorPosition(double newX, double newY, double newZ) {
        PxVec3 newVector = new PxVec3((float) newX, (float) newY, (float) newZ);
        pose.setP(newVector);
        newVector.destroy();
    }

    @Override
    public boolean onUpdatePosition(double newX, double newY, double newZ) {
        updatePosition = true;
        updateVectorPosition(newX, newY, newZ);
        return true;
    }

    @Override
    public boolean onUpdateSize(double newX, double newY, double newZ) {
        updateSize = true;
        PxVec3 newVector = new PxVec3((float) newX / 2, (float) newY / 2, (float) newZ / 2);
        boxGeometry.setHalfExtents(newVector);
        newVector.destroy();
        return true;
    }

    @Override
    public boolean onUpdateRotation(double newX, double newY, double newZ) {
        updateRotation = true;
        return true;
    }

    private double previousX;
    private double previousY;
    private double previousZ;
    private double previousW;

    @Override
    public void onPhysicsUpdate() {
        PxVec3 position;
        PxQuat quat;
        if(!isStaticShape()) {
            position = rigidDynamic.getGlobalPose().getP();
            quat = rigidDynamic.getGlobalPose().getQ();
        } else {
            position = rigidStatic.getGlobalPose().getP();
            quat = rigidStatic.getGlobalPose().getQ();
        }
        if(position.getX() != getObject().getPosition().getX() || position.getY() != getObject().getPosition().getY() || position.getZ() != getObject().getPosition().getZ()) {
            getObject().setPosition(new TransformSet(
                    position.getX(),
                    position.getY(),
                    position.getZ()
            ));
        }
        if(quat.getX() != previousX || quat.getY() != previousY || quat.getZ() != previousZ || quat.getW() != previousW) {
            double[] rot = RotationHelper.quaternionToEulerXYZ(quat.getX(), quat.getY(), quat.getZ(), quat.getW());
            previousX = quat.getX();
            previousY = quat.getY();
            previousZ = quat.getZ();
            previousW = quat.getW();
            getObject().setRotation(new TransformSet(rot[0], rot[1], rot[2]));
        }
    }

    @Override
    public void on3DRenderRequest(GraphicsManager3D g) {
        handleUpdates(g, GraphicsManager3D.ShapeType.BOX);
    }

}