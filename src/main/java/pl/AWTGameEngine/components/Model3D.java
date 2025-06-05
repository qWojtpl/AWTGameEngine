package pl.AWTGameEngine.components;

import javafx.scene.Group;
import javafx.scene.Node;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.Component3D;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.graphics.Renderable3D;
import pl.AWTGameEngine.engine.panels.Panel3D;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Sprite;

@Component3D
public class Model3D extends ObjectComponent implements Renderable3D {

    private String modelPath;

    protected Sprite sprite;
    protected boolean updatePosition = false;
    protected boolean updateSize = false;
    protected boolean updateRotation = false;
    protected boolean updateSprite = false;

    public Model3D(GameObject object) {
        super(object);
    }

    public Group getModel() {
        return ((Panel3D) getPanel()).getGraphicsManager3D().getCustomModel(getObject().getIdentifier());
    }

    @Override
    public void onAddComponent() {
        getObject().getRotation().setZ(180);
        ((Panel3D) getPanel()).getGraphicsManager3D().createCustomModel(
                new GraphicsManager3D.RenderOptions(
                        getObject().getIdentifier(),
                        getObject().getPosition(),
                        getObject().getSize(),
                        getObject().getRotation(),
                        getSprite(),
                        null
                ),
                modelPath
        );
    }

    @Override
    public void onRemoveComponent() {
        ((Panel3D) getPanel()).getGraphicsManager3D().removeCustomModel(getObject().getIdentifier());
    }

    @SerializationSetter
    public void setModelPath(String path) {
        this.modelPath = path;
    }

    @Override
    public void on3DRenderRequest(GraphicsManager3D g) {
        handleUpdates(g, g.getCustomModel(getObject().getIdentifier()));
    }

    protected void handleUpdates(GraphicsManager3D g, Node node) {
        if(updatePosition) {
            g.updatePosition(node, getObject().getPosition());
            updatePosition = false;
        }
        if(updateSize) {
            g.updateSize(node, getObject().getSize());
            updateSize = false;
        }
        if(updateRotation) {
            g.updateRotation(node, getObject().getRotation());
            updateRotation = false;
        }
        if(updateSprite) {
            g.updateSprite(node, sprite);
            updateSprite = false;
        }
    }

    @SerializationSetter
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
