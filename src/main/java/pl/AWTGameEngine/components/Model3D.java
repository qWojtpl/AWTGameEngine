
package pl.AWTGameEngine.components;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.ComponentFX;
import pl.AWTGameEngine.annotations.ComponentGL;
import pl.AWTGameEngine.annotations.FromXML;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.panels.PanelFX;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Sprite;

@ComponentFX
@ComponentGL
public class Model3D extends ObjectComponent {

    private String modelPath;

    protected Sprite sprite;
    protected boolean initialized = false;
    protected boolean updatePosition = false;
    protected boolean updateSize = false;
    protected boolean updateRotation = false;
    protected boolean updateSprite = false;

    public Model3D(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        getObject().getRotation().setZ(180);
        ((PanelFX) getPanel()).getGraphicsManager3D().createCustomModel(
                new GraphicsManager3D.RenderOptions(
                        getObject().getIdentifier(),
                        getObject().getPosition(),
                        getObject().getSize(),
                        getObject().getRotation(),
                        getObject().getQuaternionRotation(),
                        getSprite(),
                        GraphicsManager3D.ShapeType.MODEL,
                        null
                ),
                modelPath
        );
        initialized = true;
    }

    @Override
    public void onRemoveComponent() {
        ((PanelFX) getPanel()).getGraphicsManager3D().removeCustomModel(getObject().getIdentifier());
    }

    @FromXML
    public void setModelPath(String path) {
        this.modelPath = path;
    }

    @Override
    public void on3DRenderRequest(GraphicsManager3D g) {
        handleUpdates(g, GraphicsManager3D.ShapeType.MODEL);
    }

    protected void handleUpdates(GraphicsManager3D g, GraphicsManager3D.ShapeType shapeType) {
        if(!initialized) {
            return;
        }
        if(updatePosition) {
            g.updatePosition(getObject().getIdentifier(), shapeType, getObject().getPosition());
            updatePosition = false;
        }
        if(updateSize) {
            g.updateSize(getObject().getIdentifier(), shapeType, getObject().getSize());
            updateSize = false;
        }
        if(updateRotation) {
            g.updateRotation(getObject().getIdentifier(), shapeType, getObject().getRotation(), getObject().getQuaternionRotation());
            updateRotation = false;
        }
        if(updateSprite) {
            g.updateSprite(getObject().getIdentifier(), shapeType, sprite);
            updateSprite = false;
        }
    }

    @FromXML
    public void setSpriteSource(String source) {
        setSprite(Dependencies.getResourceManager().getResourceAsSprite(source));
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        updateSprite = true;
    }

}