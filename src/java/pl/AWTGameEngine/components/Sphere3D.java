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
        @ConflictsWith(Box3D.class),
        @ConflictsWith(Cylinder3D.class)
})
public class Sphere3D extends Base3DShape implements Renderable3D {

    public Sphere3D(GameObject object) {
        super(object);
    }

    @Override
    protected void createShape() {
        ((Panel3D) getPanel()).getGraphicsManager3D().createSphere(
                getObject().getIdentifier(),
                getObject().getPosition(),
                getObject().getSize(),
                getObject().getRotation(),
                getSprite()
        );
    }

    @Override
    public void on3DRenderRequest(GraphicsManager3D g) {
        Shape3D shape = g.getSphere(getObject().getIdentifier());
        handleUpdates(g, shape);
    }

}
