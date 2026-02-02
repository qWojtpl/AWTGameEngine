package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.components.management.Conflicts;
import pl.AWTGameEngine.annotations.components.management.ConflictsWith;
import pl.AWTGameEngine.annotations.components.types.ComponentFX;
import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.components.base.Base3DShape;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.panels.PanelFX;
import pl.AWTGameEngine.engine.panels.PanelGL;
import pl.AWTGameEngine.objects.GameObject;

@ComponentFX
@ComponentGL
@Conflicts({
        @ConflictsWith(Box3D.class),
        @ConflictsWith(Cylinder3D.class)
})
public class Sphere3D extends Base3DShape {

    private final GraphicsManager3D graphicsManager3D;

    public Sphere3D(GameObject object) {
        super(object);
        if(getScene().getPanel() instanceof PanelFX) {
            graphicsManager3D = ((PanelFX) getScene().getPanel()).getGraphicsManager3D();
        } else {
            graphicsManager3D = ((PanelGL) getScene().getPanel()).getGraphicsManager3D();
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

        graphicsManager3D.createSphere(options);
        initialized = true;
    }

    @Override
    protected void removeShape() {
        ((PanelFX) getScene().getPanel()).getGraphicsManager3D().removeSphere(getObject().getIdentifier());
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
    public void on3DRenderRequest(GraphicsManager3D g) {
        handleUpdates(g, GraphicsManager3D.ShapeType.SPHERE);
    }

}