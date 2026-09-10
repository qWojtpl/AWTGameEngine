package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.components.types.Component3D;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.components.base.Base3DShape;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.render.RenderOptions3D;
import pl.AWTGameEngine.objects.transform.Vector3;

@Component3D
public class Model3D extends Base3DShape {

    private Vector3 modelSize;

    public Model3D(GameObject object) {
        super(object);
    }

    @Override
    protected RenderOptions3D createShape() {
        if(graphicsManager3D == null) {
            return null;
        }

        return renderOptions
                .setPosition(getObject().getPosition())
                .setSize(modelSize == null ? getObject().getSize() : modelSize)
                .setRotation(getObject().getRotation())
                .setQuaternionRotation(getObject().getQuaternionRotation());
    }

    @Override
    public void patchRender() {
        if(renderOptions == null) {
            return;
        }
        if(modelSize != null) {
            renderOptions.setSize(modelSize);
        }
    }

    @SaveState(name = "modelSize")
    public Vector3 getModelSize() {
        return this.modelSize;
    }

    @FromXML
    public void setModelSize(Vector3 modelSize) {
        this.modelSize = modelSize;
    }

}
