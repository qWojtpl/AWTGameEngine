package pl.AWTGameEngine.custom.performance;

import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.enums.KeyCode;
import pl.AWTGameEngine.objects.GameObject;

@ComponentGL
@DefaultComponent
@WebComponent
public class StateSaverTest extends ObjectComponent {

    public StateSaverTest(GameObject object) {
        super(object);
    }

    @Override
    public void onPhysicsUpdate() {
        if(getKeyListener().hasPressedKey(KeyCode.P)) {
            getScene().saveState("./saved_state.xml", true);
        }
    }

}
