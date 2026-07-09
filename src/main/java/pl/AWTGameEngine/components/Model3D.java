package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.components.base.Base3DShape;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.helpers.RotationHelper;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.render.RenderOptions3D;
import pl.AWTGameEngine.objects.transform.QuaternionTransformSet;
import pl.AWTGameEngine.objects.transform.TransformSet;

@ComponentGL
public class Model3D extends Base3DShape {

    private RenderOptions3D options;
    private String modelPath;
    private TransformSet modelSize;

    public Model3D(GameObject object) {
        super(object);
    }

    @Override
    protected void createShape() {
        if(graphicsManager3D == null || modelPath == null) {
            return;
        }

        options = new RenderOptions3D(getObject().getIdentifier())
                .setPosition(getObject().getPosition())
                .setSize(modelSize == null ? getObject().getSize() : modelSize)
                .setRotation(getObject().getRotation())
                .setQuaternionRotation(getObject().getQuaternionRotation())
                .setSprite(getSprite())
                .setShader(getShader())
                .setColor(getColor())
                .setShapePath(modelPath);

        graphicsManager3D.createRenderable(options);

        initialized = true;
    }

    @Override
    public void patchRender() {
        if(options == null) {
            return;
        }
        if(modelSize != null) {
            updateSize = false;
        }
    }

    @FromXML
    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    @SaveState(name = "modelPath")
    public String getModelPath() {
        return this.modelPath;
    }

    @FromXML
    public void setModelSize(TransformSet modelSize) {
        this.modelSize = modelSize;
    }

    @SaveState(name = "modelSize")
    public TransformSet getModelSize() {
        return this.modelSize;
    }

}
