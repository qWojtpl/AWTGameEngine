package pl.AWTGameEngine.components.base;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.FromXML;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Sprite;

public abstract class Base3DShape extends ObjectComponent {

    protected Sprite sprite;
    protected ColorObject color;
    protected String glTexture;
    protected boolean initialized = false;
    protected boolean updatePosition = false;
    protected boolean updateSize = false;
    protected boolean updateRotation = false;
    protected boolean updateSprite = false;
    protected boolean updateColor = false;
    protected boolean updateGlTexture = false;
    private boolean staticShape = true;

    private double mass = 0.03;

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
        if(updateGlTexture) {
            g.updateGlTexture(getObject().getIdentifier(), shapeType, glTexture);
            updateGlTexture = false;
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

    public double getMass() {
        return this.mass;
    }

    public ColorObject getColor() {
        return this.color;
    }

    public void setMass(double mass) {
        this.mass = mass;
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
    public void setStaticShape(String staticShape) {
        setStaticShape(Boolean.parseBoolean(staticShape));
    }

    @FromXML
    public void setMass(String mass) {
        setMass(Double.parseDouble(mass));
    }

    @FromXML
    public void setColor(String color) {
        setColor(new ColorObject(color));
    }

    @FromXML
    public void setGlTexture(String glTexture) {
        this.glTexture = glTexture;
        updateGlTexture = true;
    }

}