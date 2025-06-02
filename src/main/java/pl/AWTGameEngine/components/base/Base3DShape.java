package pl.AWTGameEngine.components.base;

import javafx.scene.Node;
import physx.physics.PxRigidDynamic;
import physx.physics.PxRigidStatic;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.graphics.Renderable3D;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Sprite;

public abstract class Base3DShape extends ObjectComponent implements Renderable3D {

    protected Sprite sprite;
    protected ColorObject color;
    protected boolean updatePosition = false;
    protected boolean updateSize = false;
    protected boolean updateRotation = false;
    protected boolean updateSprite = false;
    protected boolean updateColor = false;
    private boolean staticShape = true;

    protected PxRigidDynamic rigidDynamic;
    protected PxRigidStatic rigidStatic;

    private double mass = 0.5;

    public Base3DShape(GameObject object) {
        super(object);
    }

    protected abstract void createShape();

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
        if(updateColor) {
            g.updateColor(node, color);
            updateColor = false;
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

    public void setMass(double mass) {
        this.mass = mass;
        if(!isStaticShape()) {
            if(rigidDynamic != null) {
                rigidDynamic.setMass((float) mass);
            }
        }
    }

    public void setColor(ColorObject color) {
        this.color = color;
        updateColor = true;
    }

    @SerializationSetter
    public void setSpriteSource(String source) {
        setSprite(Dependencies.getResourceManager().getResourceAsSprite(source));
    }

    @SerializationSetter
    public void setStaticShape(String staticShape) {
        setStaticShape(Boolean.parseBoolean(staticShape));
    }

    @SerializationSetter
    public void setMass(String mass) {
        setMass(Double.parseDouble(mass));
    }

    @SerializationSetter
    public void setColor(String color) {
        setColor(new ColorObject(color));
    }

}
