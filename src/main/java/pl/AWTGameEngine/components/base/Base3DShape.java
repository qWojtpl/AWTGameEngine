package pl.AWTGameEngine.components.base;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.panels.PanelGL;
import pl.AWTGameEngine.objects.*;
import pl.AWTGameEngine.objects.net.NetBlock;
import pl.AWTGameEngine.objects.render.AnimatedSprite;
import pl.AWTGameEngine.objects.render.Sprite;

import java.util.Objects;

public abstract class Base3DShape extends NetComponent {

    protected GraphicsManager3D graphicsManager3D;
    protected Sprite sprite = Dependencies.getResourceManager().getResourceAsSprite("sprites/default.jpg");
    protected ColorObject color;
    protected String shader = "shaders/shader";
    protected String shapePath = "models/box.obj";
    protected boolean xray = false;
    protected boolean initialized = false;
    protected boolean updatePosition = false;
    protected boolean updateSize = false;
    protected boolean updateRotation = false;
    protected boolean updateSprite = false;
    protected boolean netUpdateSprite = false;
    protected boolean updateColor = false;
    protected boolean updateShader = false;
    protected boolean updateShapePath = false;
    protected boolean updateXray = false;

    public Base3DShape(GameObject object) {
        super(object);
    }

    protected abstract void createShape();
    protected abstract void patchRender();

    protected void handleUpdates(GraphicsManager3D g) {
        if(!initialized) {
            return;
        }
        if(updatePosition) {
            g.updatePosition(getObject().getIdentifier(), getObject().getPosition());
            updatePosition = false;
        }
        if(updateSize) {
            g.updateSize(getObject().getIdentifier(), getObject().getSize());
            updateSize = false;
        }
        if(updateRotation) {
            g.updateRotation(getObject().getIdentifier(), getObject().getRotation(), getObject().getQuaternionRotation());
            updateRotation = false;
        }
        if(updateSprite) {
            if(sprite instanceof AnimatedSprite) {
                g.updateSprite(getObject().getIdentifier(), ((AnimatedSprite) sprite).requestSprite(), false);
            } else {
                g.updateSprite(getObject().getIdentifier(), sprite, true);
                updateSprite = false;
            }
        }
        if(updateShader) {
            g.updateShader(getObject().getIdentifier(), shader);
            updateShader = false;
        }
        if(updateShapePath) {
            g.updateShapePath(getObject().getIdentifier(), shapePath);
            updateShapePath = false;
        }
        if(updateColor) {
            g.updateColor(getObject().getIdentifier(), color);
            updateColor = false;
        }
        if(updateXray) {
            g.updateXray(getObject().getIdentifier(), xray);
            updateXray = false;
        }
    }

    @SaveState(name = "spriteSource")
    public Sprite getSprite() {
        return this.sprite;
    }

    public void setSprite(Sprite sprite) {
        Objects.requireNonNull(sprite);
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
    public void setXray(boolean xray) {
        this.xray = xray;
        updateXray = true;
    }

    @SaveState(name = "shader")
    public String getShader() {
        return this.shader;
    }

    @FromXML
    public void setShader(String shader) {
        this.shader = shader;
        updateShader = true;
    }

    @SaveState(name = "shapePath")
    public String getShapePath() {
        return this.shapePath;
    }

    @FromXML
    public void setShapePath(String shapePath) {
        this.shapePath = shapePath;
        updateShapePath = true;
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
        return new NetBlock(getObject().getIdentifier(), this.getClass(), getSprite().getImagePath());
    }

    @Override
    public void onSynchronizeReceived(String data) {
        setSpriteSource(data);
    }

    @Override
    public void clearNetCache() {
        netUpdateSprite = true;
    }

    @Override
    public void onAddComponent() {
        this.graphicsManager3D = ((PanelGL) getScene().getPanel()).getGraphicsManager3D();
        createShape();
    }

    @Override
    public void onRemoveComponent() {
        if(graphicsManager3D == null) {
            return;
        }

        graphicsManager3D.removeRenderable(getObject().getIdentifier());
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
    public void onUpdateRotation() {
        updateRotation = true;
    }

    @Override
    public void on3DRenderRequest(GraphicsManager3D g) {
        patchRender();
        handleUpdates(g);
    }

}