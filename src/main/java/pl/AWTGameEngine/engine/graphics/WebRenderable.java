package pl.AWTGameEngine.engine.graphics;

import pl.AWTGameEngine.annotations.EventMethod;

public interface WebRenderable {

    @EventMethod
    void onWebRenderRequest(WebGraphicsManager g);

}
