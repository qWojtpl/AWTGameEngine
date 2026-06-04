package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.components.management.Conflicts;
import pl.AWTGameEngine.annotations.components.management.ConflictsWith;
import pl.AWTGameEngine.annotations.components.types.ComponentFX;
import pl.AWTGameEngine.components.base.Base3DShape;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.panels.PanelFX;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.render.RenderOptions3D;

@ComponentFX
@Conflicts({
        @ConflictsWith(Sphere3D.class),
        @ConflictsWith(Box3D.class)
})
public class Cylinder3D extends Base3DShape {

    public Cylinder3D(GameObject object) {
        super(object);
    }

    @Override
    protected void createShape() {
        ((PanelFX) getScene().getPanel()).getGraphicsManager3D().createCylinder(new RenderOptions3D(getObject().getIdentifier())
                .setPosition(getObject().getPosition())
                .setSize(getObject().getSize())
                .setRotation(getObject().getRotation())
                .setQuaternionRotation(getObject().getQuaternionRotation())
                .setSprite(getSprite())
                .setShapeType(GraphicsManager3D.ShapeType.CYLINDER)
                .setShader(getShader())
                .setColor(getColor()));

        initialized = true;
    }

    @Override
    protected void removeShape() {
        ((PanelFX) getScene().getPanel()).getGraphicsManager3D().removeCylinder(getObject().getIdentifier());
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
        handleUpdates(g, GraphicsManager3D.ShapeType.CYLINDER);
    }

}