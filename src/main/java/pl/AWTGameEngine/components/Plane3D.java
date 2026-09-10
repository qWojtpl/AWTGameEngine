package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.components.management.Unique;
import pl.AWTGameEngine.annotations.components.types.Component3D;
import pl.AWTGameEngine.components.base.Base3DShape;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.render.RenderOptions3D;

@Component3D
@Unique
public class Plane3D extends Base3DShape {

    public Plane3D(GameObject object) {
        super(object);
    }

    @Override
    protected RenderOptions3D createShape() {

        if(graphicsManager3D == null) {
            return null;
        }

        return renderOptions
                .setPosition(getObject().getPosition())
                .setSize(getObject().getSize())
                .setRotation(getObject().getRotation())
                .setQuaternionRotation(getObject().getQuaternionRotation())
                .setShapePath("models/plane.obj");
    }

    @Override
    protected void patchRender() {

    }

}
