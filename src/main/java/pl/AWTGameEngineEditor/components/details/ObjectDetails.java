package pl.AWTGameEngineEditor.components.details;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.base.HTMLComponent;
import pl.AWTGameEngine.objects.GameObject;

@WebComponent
public class ObjectDetails extends HTMLComponent {

    public ObjectDetails(GameObject object) {
        super(object);
    }

    @Override
    public String getRenderString() {
        return String.join("\n", Dependencies.getResourceManager().getResource("webview/editor/objectdetails.html"));
    }


}
