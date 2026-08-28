
package pl.AWTGameEngine.engine.graphics;

import pl.AWTGameEngine.objects.render.RenderOptions3D;

public abstract class GraphicsManager3D {

    public abstract void preloadShape(String path);

    public abstract void createRenderable(RenderOptions3D options);

    public abstract void removeRenderable(String identifier);

    public abstract void freeTexture(RenderOptions3D options);

    public abstract RenderOptions3D getRenderable(String identifier);

}