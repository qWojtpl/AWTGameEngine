package pl.AWTGameEngine.components;

import javafx.scene.shape.Shape3D;
import pl.AWTGameEngine.annotations.*;
import pl.AWTGameEngine.components.base.Base3DShape;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.graphics.Renderable3D;
import pl.AWTGameEngine.engine.panels.Panel3D;
import pl.AWTGameEngine.objects.GameObject;

@Component3D
@Conflicts({
        @ConflictsWith(Sphere3D.class),
        @ConflictsWith(Box3D.class)
})
public class Cylinder3D extends Base3DShape implements Renderable3D {

    public Cylinder3D(GameObject object) {
        super(object);
    }

    @Override
    protected void createShape() {
        ((Panel3D) getPanel()).getGraphicsManager3D().createCylinder(new GraphicsManager3D.RenderOptions(
                getObject().getIdentifier(),
                getObject().getPosition(),
                getObject().getSize(),
                getObject().getRotation(),
                getSprite(),
                null)
        );
    }

    @Override
    public void onAddComponent() {
        createShape();
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
        Shape3D shape = g.getCylinder(getObject().getIdentifier());
        handleUpdates(g, shape);
    }

}
