package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.*;
import pl.AWTGameEngine.components.base.Base3DShape;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.panels.PanelFX;
import pl.AWTGameEngine.engine.panels.PanelGL;
import pl.AWTGameEngine.objects.GameObject;

@ComponentFX
@ComponentGL
@Unique
@Conflicts({
        @ConflictsWith(Sphere3D.class),
        @ConflictsWith(Cylinder3D.class)
})
public class Box3D extends Base3DShape {

    private final GraphicsManager3D graphicsManager3D;

    public Box3D(GameObject object) {
        super(object);
        if(getPanel() instanceof PanelFX) {
            graphicsManager3D = ((PanelFX) getPanel()).getGraphicsManager3D();
        } else {
            graphicsManager3D = ((PanelGL) getPanel()).getGraphicsManager3D();
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

        if(glTexture != null) {
            options.setGlTexture(glTexture);
        }

        graphicsManager3D.createBox(options);
        initialized = true;
    }

    @FromXML
    public void setGlTexture(String texture) {
        super.setGlTexture(texture);
    }

    @Override
    protected void removeShape() {
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
    public boolean onUpdateRotation(double newX, double newY, double newZ) {
        updateRotation = true;
        return true;
    }

    @Override
    public void on3DRenderRequest(GraphicsManager3D g) {
        handleUpdates(g, GraphicsManager3D.ShapeType.BOX);
    }

}