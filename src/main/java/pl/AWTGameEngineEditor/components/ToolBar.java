package pl.AWTGameEngineEditor.components;

import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.base.HTMLFileComponent;
import pl.AWTGameEngine.objects.GameObject;

@WebComponent
public class ToolBar extends HTMLFileComponent {

    public ToolBar(GameObject object) {
        super(object);
    }

}
