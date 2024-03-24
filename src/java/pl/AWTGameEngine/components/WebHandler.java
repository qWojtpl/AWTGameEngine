package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.OnlyOnWeb;
import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.engine.graphics.WebRenderable;
import pl.AWTGameEngine.objects.GameObject;

// Component is automatically added to every GameObject if render engine is set to web
@OnlyOnWeb
@Unique
public class WebHandler extends ObjectComponent implements WebRenderable {

    private boolean updatePosition = true;
    private boolean updateSize = true;
    private boolean updateRotation = true;

    public WebHandler(GameObject object) {
        super(object);
    }

    @Override
    public void onWebRenderRequest(WebGraphicsManager g) {
        if(updatePosition) {
            g.updatePosition(getObject());
        }
        if(updateSize) {
            g.updateSize(getObject());
        }
        if(updateRotation) {
            g.updateRotation(getObject());
        }
        updatePosition = false;
        updateSize = false;
        updateRotation = false;
    }

    @Override
    public boolean onUpdatePosition(int newX, int newY) {
        updatePosition = true;
        return true;
    }

    @Override
    public boolean onUpdateSize(int newX, int newY) {
        updateSize = true;
        return true;
    }

    @Override
    public boolean onUpdateRotation(int newRotation) {
        updateRotation = true;
        return true;
    }

    @Override
    public void onUpdateCameraPosition(int newX, int newY) {
        updatePosition = true;
    }

}
