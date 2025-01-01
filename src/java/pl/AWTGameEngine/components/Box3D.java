package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Component3D;
import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.graphics.Renderable3D;
import pl.AWTGameEngine.objects.GameObject;

@Component3D
@Unique
public class Box3D extends ObjectComponent implements Renderable3D {

    public Box3D(GameObject object) {
        super(object);
    }

    @Override
    public void on3DRenderRequest(GraphicsManager3D g) {
        g.renderBox(getObject().getIdentifier(), getObject().getPosition(), getObject().getSize(), getObject().getRotation());
    }
}
