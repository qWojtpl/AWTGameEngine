
package pl.AWTGameEngine.engine.graphics;

import pl.AWTGameEngine.objects.*;

public abstract class GraphicsManager3D {

    public abstract void createBox(RenderOptions3D options);

    public abstract void removeBox(String identifier);

    public abstract void createSphere(RenderOptions3D options);

    public abstract void removeSphere(String identifier);

    public abstract void createCylinder(RenderOptions3D options);

    public abstract void removeCylinder(String identifier);

    public abstract void createCustomModel(RenderOptions3D options, String modelPath);

    public abstract void removeCustomModel(String identifier);

    public abstract void updatePosition(String identifier, ShapeType shape, TransformSet position);

    public abstract void updateSize(String identifier, ShapeType shape, TransformSet scale);

    public abstract void updateRotation(String identifier, ShapeType shape, TransformSet rotation, QuaternionTransformSet quaternionRotation);

    public abstract void updateSprite(String identifier, ShapeType shape, Sprite sprite);

    public abstract void updateShader(String identifier, ShapeType shape, String shader);

    public abstract void updateColor(String identifier, ShapeType shape, ColorObject color);

    public abstract void updateXray(String identifier, ShapeType shape, boolean xray);

    public enum ShapeType {
        BOX,
        CYLINDER,
        SPHERE,
        MODEL
    }

}