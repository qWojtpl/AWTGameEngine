package pl.AWTGameEngine.components.base;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.Shaders;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.panels.Panel3D;
import pl.AWTGameEngine.objects.*;
import pl.AWTGameEngine.objects.net.NetBlock;
import pl.AWTGameEngine.objects.render.AnimatedSprite;
import pl.AWTGameEngine.objects.render.Material;
import pl.AWTGameEngine.objects.render.RenderOptions3D;
import pl.AWTGameEngine.objects.render.shaders.DefaultShader;
import pl.AWTGameEngine.objects.render.shaders.Shader;

public abstract class Base3DShape extends NetComponent {

    protected GraphicsManager3D graphicsManager3D;
    protected RenderOptions3D renderOptions = new RenderOptions3D(getObject().getIdentifier())
            .setShader(Shaders.of(getWindow(), DefaultShader.class));
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
        if(renderOptions.getMaterial().getSprite() instanceof AnimatedSprite) {
            renderOptions.getMaterial().setSprite(((AnimatedSprite) renderOptions.getMaterial().getSprite()).requestSprite());
        }
    }

    @SaveState(name = "material")
    public Material getMaterial() {
        return this.renderOptions.getMaterial();
    }

    @FromXML
    public void setMaterial(Material material) {
        if(material == null) {
            return;
        }
        if(graphicsManager3D != null) {
            graphicsManager3D.freeTexture(renderOptions);
        }
        renderOptions.setMaterial(material);
        netUpdateSprite = true;
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
        if(getMaterial().getSprite() == null) {
            return new NetBlock();
        }
        return new NetBlock(getObject().getIdentifier(), this.getClass(), getMaterial().getSprite().getImagePath());
    }

    @Override
    public void onSynchronizeReceived(String data) {
        renderOptions.getMaterial().setSprite(Dependencies.getResourceManager().getResourceAsSprite(data));
    }

    @Override
    public void clearNetCache() {
        netUpdateSprite = true;
    }

    @Override
    public void onAddComponent() {
        this.graphicsManager3D = ((Panel3D) getScene().getPanel()).getGraphicsManager3D();
        this.renderOptions = createShape();
        graphicsManager3D.createRenderable(renderOptions);
        graphicsManager3D.preloadShape(renderOptions.getShapePath());
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
        if(renderOptions.getRotation() != null) {
            if(!renderOptions.getRotation().equals(getObject().getRotation())) {
                renderOptions.setRotation(renderOptions.getRotation().clone());
            }
        }
        if(renderOptions.getQuaternionRotation() != null) {
            if(!renderOptions.getQuaternionRotation().equals(getObject().getQuaternionRotation())) {
                renderOptions.setQuaternionRotation(renderOptions.getQuaternionRotation().clone());
            }
        }
    }

    @Override
    public void on3DRenderRequest(GraphicsManager3D g) {
        patchRender();
        handleUpdates(g);
    }

}