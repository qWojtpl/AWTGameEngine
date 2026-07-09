package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.components.management.Unique;
import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.components.base.Base3DShape;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.render.RenderOptions3D;

@ComponentGL
@Unique
public class Box3D extends Base3DShape {

    public Box3D(GameObject object) {
        super(object);
    }

    @Override
    protected void createShape() {

        if(graphicsManager3D == null) {
            return;
        }

        RenderOptions3D options = new RenderOptions3D(getObject().getIdentifier())
                .setPosition(getObject().getPosition())
                .setSize(getObject().getSize())
                .setRotation(getObject().getRotation())
                .setQuaternionRotation(getObject().getQuaternionRotation())
                .setSprite(getSprite())
                .setShader(getShader())
                .setColor(getColor())
                .setShapePath("models/box.obj");

        graphicsManager3D.createRenderable(options);
        initialized = true;
    }

    @Override
    protected void patchRender() {

    }

}