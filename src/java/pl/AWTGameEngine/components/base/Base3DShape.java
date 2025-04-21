package pl.AWTGameEngine.components.base;

import javafx.scene.shape.Shape3D;
import physx.common.PxVec3;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Sprite;

public abstract class Base3DShape extends ObjectComponent {

    protected Sprite sprite;
    protected boolean updatePosition = false;
    protected boolean updateSize = false;
    protected boolean updateRotation = false;
    protected boolean updateSprite = false;
    private boolean staticShape = true;

    public Base3DShape(GameObject object) {
        super(object);
    }

    protected abstract void createShape();

    protected void handleUpdates(GraphicsManager3D g, Shape3D shape) {
        if(updatePosition) {
            g.updatePosition(shape, getObject().getPosition());
            updatePosition = false;
        }
        if(updateSize) {
            g.updateSize(shape, getObject().getSize());
            updateSize = false;
        }
        if(updateRotation) {
            g.updateRotation(shape, getObject().getRotation());
            updateRotation = false;
        }
        if(updateSprite) {
            g.updateSprite(shape, sprite);
            updateSprite = false;
        }
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public boolean isStaticShape() {
        return this.staticShape;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        updateSprite = true;
    }

    public void setStaticShape(boolean staticShape) {
        this.staticShape = staticShape;
    }

    @SerializationSetter
    public void setSpriteSource(String source) {
        setSprite(Dependencies.getResourceManager().getResourceAsSprite(source));
    }

    @SerializationSetter
    public void setStaticShape(String staticShape) {
        setStaticShape(Boolean.parseBoolean(staticShape));
    }

}
