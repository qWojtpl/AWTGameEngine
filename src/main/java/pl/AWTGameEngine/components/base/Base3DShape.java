package pl.AWTGameEngine.components.base;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.FromXML;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.NetBlock;
import pl.AWTGameEngine.objects.Sprite;

public abstract class Base3DShape extends ObjectComponent {

    protected Sprite sprite;
    protected ColorObject color;
    protected boolean initialized = false;
    protected boolean updatePosition = false;
    protected boolean updateSize = false;
    protected boolean updateRotation = false;
    protected boolean updateSprite = false;
    protected boolean netUpdateSprite = false;
    protected boolean updateColor = false;

    public Base3DShape(GameObject object) {
        super(object);
    }

    protected abstract void createShape();

    protected abstract void removeShape();

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
        if(updateColor) {
            g.updateColor(getObject().getIdentifier(), shapeType, color);
            updateColor = false;
        }
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        updateSprite = true;
        netUpdateSprite = true;
    }

    public ColorObject getColor() {
        return this.color;
    }

    public void setColor(ColorObject color) {
        this.color = color;
        updateColor = true;
    }

    @FromXML
    public void setSpriteSource(String source) {
        setSprite(Dependencies.getResourceManager().getResourceAsSprite(source));
    }

    @FromXML
    public void setColor(String color) {
        setColor(new ColorObject(color));
    }

    @Override
    public boolean canSynchronize() {
        return netUpdateSprite;
    }

    @Override
    public NetBlock onSynchronize() {
        netUpdateSprite = false;
        if(getSprite() == null) {
            return new NetBlock();
        }
        return new NetBlock(getObject().getIdentifier(), this.getClass().getName(), getSprite().getImagePath());
    }

    @Override
    public void onSynchronizeReceived(String data) {
        setSpriteSource(data);
    }

    @Override
    public void clearNetCache() {
        netUpdateSprite = true;
    }

}