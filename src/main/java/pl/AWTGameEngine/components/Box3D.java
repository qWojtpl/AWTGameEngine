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
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.graphics.Renderable3D;
import pl.AWTGameEngine.engine.panels.PanelFX;
import pl.AWTGameEngine.engine.panels.PanelGL;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.QuaternionTransformSet;
import pl.AWTGameEngine.objects.RigidBody;
import pl.AWTGameEngine.objects.TransformSet;

@ComponentFX
@ComponentGL
@Unique
@Conflicts({
        @ConflictsWith(Sphere3D.class),
        @ConflictsWith(Cylinder3D.class)
})
public class Box3D extends Base3DShape implements Renderable3D {

    private final GraphicsManager3D graphicsManager3D;

    private RigidBody rigidBody;

    public Box3D(GameObject object) {
        super(object);
        if(getPanel() instanceof PanelFX) {
            graphicsManager3D = ((PanelFX) getPanel()).getGraphicsManager3D();
        } else {
            graphicsManager3D = ((PanelGL) getPanel()).getGraphicsManager3D();
        }
    }

    @Override
    protected void createShape() {

        GraphicsManager3D.RenderOptions options = new GraphicsManager3D.RenderOptions(
                getObject().getIdentifier(),
                getObject().getPosition(),
                getObject().getSize(),
                getObject().getRotation(),
                getObject().getQuaternionRotation(),
                getSprite(),
                GraphicsManager3D.ShapeType.BOX,
                getColor()
        );

        if(glTexture != null) {
            options.setGlTexture(glTexture);
        }

        graphicsManager3D.createBox(options);
        initialized = true;
    }

    @Override
    protected void removeShape() {
        graphicsManager3D.removeBox(getObject().getIdentifier());
    }

    @Override
    public void onAddComponent() {
        createShape();
        if(isStaticShape()) {
            rigidBody = new RigidBody.Static(this);
        } else {
            rigidBody = new RigidBody.Dynamic(this);
            rigidBody.setMass(getMass());
        }
        rigidBody.initialize();
    }

    @Override
    public void onRemoveComponent() {
        removeShape();
        rigidBody.destroy();
    }

    @Override
    public boolean onUpdatePosition(double newX, double newY, double newZ) {
        updatePosition = true;
        rigidBody.updatePosition(new TransformSet(newX, newY, newZ));
        return true;
    }

    @Override
    public boolean onUpdateSize(double newX, double newY, double newZ) {
        updateSize = true;
        rigidBody.updateSize(new TransformSet(newX, newY, newZ));
        return true;
    }

    @Override
    public boolean onUpdateRotation(double newX, double newY, double newZ) {
        updateRotation = true;
        return true;
    }

    @Override
    public void onPhysicsUpdate() {
        rigidBody.physicsUpdate();
    }

    @Override
    public void on3DRenderRequest(GraphicsManager3D g) {
        handleUpdates(g, GraphicsManager3D.ShapeType.BOX);
    }

}