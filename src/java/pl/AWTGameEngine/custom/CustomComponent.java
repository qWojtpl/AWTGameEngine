package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.annotations.Component3D;
import pl.AWTGameEngine.annotations.WebComponent;
import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.objects.GameObject;

@WebComponent
@Component3D
public class CustomComponent extends ObjectComponent {

    private float i = 0;

    public CustomComponent(GameObject object) {
        super(object);
    }

    @Override
    public void onUpdate() {
        i += 0.5f;
        getObject().getRotation().setY((int) i);
    }

    @Override
    public void onMouseClick() {
        Logger.log(1, "Clicked");
    }

}
