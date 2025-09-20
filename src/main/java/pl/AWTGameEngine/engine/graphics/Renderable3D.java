package pl.AWTGameEngine.engine.graphics;

import pl.AWTGameEngine.annotations.EventMethod;

public interface Renderable3D {

    @EventMethod
    void on3DRenderRequest(GraphicsManager3D g);

}
