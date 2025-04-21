package pl.AWTGameEngine.components;

import javafx.scene.shape.Shape3D;
import physx.common.PxIDENTITYEnum;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.geometry.PxBoxGeometry;
import physx.physics.*;
import pl.AWTGameEngine.annotations.*;
import pl.AWTGameEngine.components.base.Base3DShape;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.graphics.Renderable3D;
import pl.AWTGameEngine.engine.panels.Panel3D;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Sprite;
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
    private PxRigidDynamic rigidDynamic;
    private PxRigidStatic rigidStatic;
    private final PxTransform pose = new PxTransform(PxIDENTITYEnum.PxIdentity);
    private final PxFilterData filterData = new PxFilterData(1, 1, 0, 0);

    public Box3D(GameObject object) {
        super(object);
        physXManager = ((Panel3D) getPanel()).getPhysXManager();
    }

    @Override
    protected void createShape() {
        ((Panel3D) getPanel()).getGraphicsManager3D().createBox(
                getObject().getIdentifier(),
                getObject().getPosition(),
                getObject().getSize(),
                getObject().getRotation(),
                getSprite()
        );
    }

    @Override
    public void onAddComponent() {
        PxPhysics physics = physXManager.getPxPhysics();
        boxGeometry = new PxBoxGeometry(
                (float) getObject().getSize().getX() / 2,
                (float) getObject().getSize().getY() / 2,
                (float) getObject().getSize().getZ() / 2
        );
        material = physics.createMaterial(0.5f, 0.5f, 0.5f);
        shape = physics.createShape(boxGeometry, material, true, physXManager.getShapeFlags());
        shape.setSimulationFilterData(filterData);
        if(isStaticShape()) {
            rigidStatic = physics.createRigidStatic(pose);
            rigidStatic.attachShape(shape);
            physXManager.getPxScene().addActor(rigidStatic);
        } else {
            rigidDynamic = physics.createRigidDynamic(pose);
            rigidDynamic.attachShape(shape);
            physXManager.getPxScene().addActor(rigidDynamic);
        }
    }

    @Override
    public void onRemoveComponent() {
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

    @Override
    public boolean onUpdatePosition(double newX, double newY, double newZ) {
        super.onUpdatePosition(newX, newY, newZ);
        PxVec3 newVector = new PxVec3((float) newX, (float) newY, (float) newZ);
        pose.setP(newVector);
        newVector.destroy();
        return true;
    }

    @Override
    public boolean onUpdateSize(double newX, double newY, double newZ) {
        super.onUpdateSize(newX, newY, newZ);
        PxVec3 newVector = new PxVec3((float) newX / 2, (float) newY / 2, (float) newZ / 2);
        boxGeometry.setHalfExtents(newVector);
        newVector.destroy();
        return true;
    }

    @Override
    public void onUpdate() {
        if(!isStaticShape()) {
            physXManager.getPxScene().simulate(1f/100000f);
            physXManager.getPxScene().fetchResults(true);
            getObject().setPosition(new TransformSet(
                    rigidDynamic.getGlobalPose().getP().getX(),
                    -rigidDynamic.getGlobalPose().getP().getY(),
                    rigidDynamic.getGlobalPose().getP().getZ()
            ));
            //System.out.println(getObject().getPosition());
        } else {
            getObject().setPosition(new TransformSet(
                    rigidStatic.getGlobalPose().getP().getX(),
                    -rigidStatic.getGlobalPose().getP().getY(),
                    rigidStatic.getGlobalPose().getP().getZ()
            ));
        }

    }

    @Override
    public void on3DRenderRequest(GraphicsManager3D g) {
        Shape3D shape = g.getBox(getObject().getIdentifier());
        handleUpdates(g, shape);
    }

}
