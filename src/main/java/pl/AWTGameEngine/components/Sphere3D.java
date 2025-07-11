package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.*;
import pl.AWTGameEngine.components.base.Base3DShape;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.graphics.Renderable3D;
import pl.AWTGameEngine.engine.panels.Panel3D;
import pl.AWTGameEngine.objects.GameObject;

@Component3D
@Conflicts({
        @ConflictsWith(Box3D.class),
        @ConflictsWith(Cylinder3D.class)
})
public class Sphere3D extends Base3DShape implements Renderable3D {

    public Sphere3D(GameObject object) {
        super(object);
    }

    @Override
    protected void createShape() {
        ((Panel3D) getPanel()).getGraphicsManager3D().createSphere(new GraphicsManager3D.RenderOptions(
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
        ((Panel3D) getPanel()).getGraphicsManager3D().removeSphere(getObject().getIdentifier());
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