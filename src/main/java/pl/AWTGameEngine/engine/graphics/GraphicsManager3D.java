
package pl.AWTGameEngine.engine.graphics;

import pl.AWTGameEngine.objects.*;
import pl.AWTGameEngine.objects.render.RenderOptions3D;
import pl.AWTGameEngine.objects.render.Sprite;
import pl.AWTGameEngine.objects.transform.QuaternionTransformSet;
import pl.AWTGameEngine.objects.transform.TransformSet;

public abstract class GraphicsManager3D {

    public abstract void createRenderable(RenderOptions3D options);

    public abstract void removeRenderable(String identifier);

    public abstract void updatePosition(String identifier, TransformSet position);

    public abstract void updateSize(String identifier, TransformSet scale);

    public abstract void updateRotation(String identifier, TransformSet rotation, QuaternionTransformSet quaternionRotation);

    public abstract void updateSprite(String identifier, Sprite sprite);

    public abstract void updateShader(String identifier, String shader);

    public abstract void updateShapePath(String identifier, String shapePath);

    public abstract void updateColor(String identifier, ColorObject color);

    public abstract void updateXray(String identifier, boolean xray);

}