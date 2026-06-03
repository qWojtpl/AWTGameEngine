package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.components.management.Unique;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.objects.GameObject;

/** Component is automatically added to every <code>GameObject</code> when <code>RenderEngine</code> is set to web.
 * Handles communication between <code>GameObject</code> and <code>WebGraphicsManager</code>.
 */
@WebComponent
@Unique
public class WebHandler extends ObjectComponent {

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
    public boolean onUpdatePosition(double newX, double newY) {
        updatePosition = true;
        return true;
    }

    @Override
    public boolean onUpdateSize(double newX, double newY) {
        updateSize = true;
        return true;
    }

    @Override
    public void onUpdateRotation() {
        updateRotation = true;
    }

    @Override
    public void onUpdateCameraPosition(double newX, double newY) {
        updatePosition = true;
    }

    @Override
    public void onWindowResize(int newWidth, int newHeight) {
        updatePosition = true;
        updateSize = true;
    }

}
