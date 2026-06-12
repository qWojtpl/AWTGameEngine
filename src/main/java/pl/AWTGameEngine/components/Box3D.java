package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.components.management.Conflicts;
import pl.AWTGameEngine.annotations.components.management.ConflictsWith;
import pl.AWTGameEngine.annotations.components.management.Unique;
import pl.AWTGameEngine.annotations.components.types.ComponentFX;
import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.components.base.Base3DShape;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.panels.PanelFX;
import pl.AWTGameEngine.engine.panels.PanelGL;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.render.RenderOptions3D;

@ComponentFX
@ComponentGL
@Unique
@Conflicts({
        @ConflictsWith(Sphere3D.class),
        @ConflictsWith(Cylinder3D.class)
})
public class Box3D extends Base3DShape {

    private GraphicsManager3D graphicsManager3D;

    public Box3D(GameObject object) {
        super(object);
    }

    @Override
    protected void createShape() {

        if(getScene().getPanel() instanceof PanelFX) {
            graphicsManager3D = ((PanelFX) getScene().getPanel()).getGraphicsManager3D();
        } else if(getScene().getPanel() instanceof PanelGL) {
            graphicsManager3D = ((PanelGL) getScene().getPanel()).getGraphicsManager3D();
        }

        if(graphicsManager3D == null) {
            return;
        }

        RenderOptions3D options = new RenderOptions3D(getObject().getIdentifier())
                .setPosition(getObject().getPosition())
                .setSize(getObject().getSize())
                .setRotation(getObject().getRotation())
                .setQuaternionRotation(getObject().getQuaternionRotation())
                .setSprite(getSprite())
                .setShapeType(GraphicsManager3D.ShapeType.BOX)
                .setShader(getShader())
                .setColor(getColor())
                .setShapePath("models/box.obj");

        graphicsManager3D.createBox(options);
        initialized = true;
    }

    @Override
    protected void removeShape() {

        if(graphicsManager3D == null) {
            return;
        }

        graphicsManager3D.removeBox(getObject().getIdentifier());
    }

    @Override
    public void onAddComponent() {
        createShape();
    }

    @Override
    public void onRemoveComponent() {
        removeShape();
    }

    @Override
    public boolean onUpdatePosition(double newX, double newY, double newZ) {
        updatePosition = true;
        return true;
    }

    @Override
    public boolean onUpdateSize(double newX, double newY, double newZ) {
        updateSize = true;
        return true;
    }

    @Override
    public void onUpdateRotation() {
        updateRotation = true;
    }

    @Override
    public void on3DRenderRequest(GraphicsManager3D g) {
        handleUpdates(g, GraphicsManager3D.ShapeType.BOX);
    }

}