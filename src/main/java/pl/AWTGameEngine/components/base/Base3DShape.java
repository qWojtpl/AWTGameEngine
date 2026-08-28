package pl.AWTGameEngine.components.base;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.engine.Shaders;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.panels.PanelGL;
import pl.AWTGameEngine.objects.*;
import pl.AWTGameEngine.objects.net.NetBlock;
import pl.AWTGameEngine.objects.render.AnimatedSprite;
import pl.AWTGameEngine.objects.render.RenderOptions3D;
import pl.AWTGameEngine.objects.render.Sprite;
import pl.AWTGameEngine.objects.render.shaders.DefaultShader;
import pl.AWTGameEngine.objects.render.shaders.Shader;

public abstract class Base3DShape extends NetComponent {

    protected GraphicsManager3D graphicsManager3D;
    protected RenderOptions3D renderOptions = new RenderOptions3D(getObject().getIdentifier())
            .setShader(Shaders.of(DefaultShader.class))
            .setSprite(Dependencies.getResourceManager().getResourceAsSprite("sprites/default.jpg"));
    protected boolean initialized = false;
    protected boolean netUpdateSprite = false;

    public Base3DShape(GameObject object) {
        super(object);
    }

    protected abstract RenderOptions3D createShape();
    protected abstract void patchRender();

    protected void handleUpdates(GraphicsManager3D g) {
        if(renderOptions == null) {
            return;
        }
        if(renderOptions.getSprite() instanceof AnimatedSprite) {
            renderOptions.setSprite(((AnimatedSprite) renderOptions.getSprite()).requestSprite());
        }
    }

    @SaveState(name = "sprite")
    public Sprite getSprite() {
        return this.renderOptions.getSprite();
    }

    @FromXML
    public void setSprite(Sprite sprite) {
        if(graphicsManager3D != null) {
            graphicsManager3D.freeTexture(renderOptions);
        }
        renderOptions.setSprite(sprite);
        netUpdateSprite = true;
    }

    public ColorObject getColor() {
        return this.renderOptions.getColor();
    }

    public void setColor(ColorObject color) {
        this.renderOptions.setColor(color);
    }

    @FromXML
    public void setXray(boolean xray) {
        this.renderOptions.setXrayRender(xray);
    }

    @SaveState(name = "shader")
    public Shader getShader() {
        return this.renderOptions.getShader();
    }

    @FromXML
    public void setShader(Shader shader) {
        this.renderOptions.setShader(shader);
    }

    @SaveState(name = "shapePath")
    public String getShapePath() {
        return this.renderOptions.getShapePath();
    }

    @FromXML
    public void setShapePath(String shapePath) {
        this.renderOptions.setShapePath(shapePath);
    }

    @FromXML
    public void setColor(String color) {
        setColor(new ColorObject(color));
    }

    @SaveState(name = "repeatTexture")
    public int getRepeatTexture() {
        return this.renderOptions.getRepeatTexture();
    }

    @FromXML
    public void setRepeatTexture(int repeatTexture) {
        this.renderOptions.setRepeatTexture(repeatTexture);
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
        setSprite(Dependencies.getResourceManager().getResourceAsSprite(data));
    }

    @Override
    public void clearNetCache() {
        netUpdateSprite = true;
    }

    @Override
    public void onAddComponent() {
        this.graphicsManager3D = ((PanelGL) getScene().getPanel()).getGraphicsManager3D();
        this.renderOptions = createShape();
        graphicsManager3D.createRenderable(renderOptions);
        graphicsManager3D.preloadShape(graphicsManager3D.getRenderable(getObject().getIdentifier()).getShapePath());
    }

    @Override
    public void onRemoveComponent() {
        if(graphicsManager3D == null) {
            return;
        }
        graphicsManager3D.removeRenderable(renderOptions.getIdentifier());
    }

    @Override
    public boolean onUpdatePosition(double newX, double newY, double newZ) {
        if(renderOptions.getPosition().equals(getObject().getPosition())) {
            return true;
        }
        renderOptions.setPosition(renderOptions.getPosition().set(newX, newY, newZ));
        return true;
    }

    @Override
    public boolean onUpdateSize(double newX, double newY, double newZ) {
        if(renderOptions.getSize().equals(getObject().getSize())) {
            return true;
        }
        renderOptions.setSize(renderOptions.getSize().set(newX, newY, newZ));
        return true;
    }

    @Override
    public void onUpdateRotation() {
        if(!renderOptions.getRotation().equals(getObject().getRotation())) {
            renderOptions.setRotation(renderOptions.getRotation().clone());
        }
        if(!renderOptions.getQuaternionRotation().equals(getObject().getQuaternionRotation())) {
            renderOptions.setQuaternionRotation(renderOptions.getQuaternionRotation().clone());
        }
    }

    @Override
    public void on3DRenderRequest(GraphicsManager3D g) {
        patchRender();
        handleUpdates(g);
    }

}