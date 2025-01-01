package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.annotations.ComponentMeta;
import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.objects.GameObject;

public class CustomComponent extends ObjectComponent {

    public CustomComponent(GameObject object) {
        super(object);
    }

    @Override
    public void onMouseClick() {
        Logger.log(1, "Clicked");
    }

}
