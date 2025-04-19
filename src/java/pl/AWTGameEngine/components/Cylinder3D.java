package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Component3D;
import pl.AWTGameEngine.components.base.Base3DShape;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.graphics.Renderable3D;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Sprite;

@Component3D
public class Cylinder3D extends Base3DShape implements Renderable3D {

    public Cylinder3D(GameObject object) {
        super(object);
    }

    @Override
    public void on3DRenderRequest(GraphicsManager3D g) {
        Sprite newSprite = null;
        if(updateSprite) {
            newSprite = sprite;
            updateSprite = false;
        }
        g.renderCylinder(getObject().getIdentifier(), getObject().getPosition(), getObject().getSize(), getObject().getRotation(), newSprite);
    }

}
