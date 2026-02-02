package pl.AWTGameEngine.components.base;

import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.objects.GameObject;

@WebComponent
public abstract class HTMLComponent extends ObjectComponent {

    private String lastString = "";

    public HTMLComponent(GameObject object) {
        super(object);
    }

    public abstract String getRenderString();

    @Override
    public void onWebRenderRequest(WebGraphicsManager g) {
        String renderString = getRenderString();
        if(lastString.equals(renderString)) {
            return;
        }
        g.execute("document.getElementById('" + getObject().getIdentifier() + "').innerHTML = `" + renderString + "`");
        lastString = renderString;
    }

}
