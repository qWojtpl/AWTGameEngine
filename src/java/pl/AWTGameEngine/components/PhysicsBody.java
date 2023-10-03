package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.objects.GameObject;

@Unique
public class PhysicsBody extends ObjectComponent {

    public PhysicsBody(GameObject object) {
        super(object);
    }

    @Override
    public void onPreUpdate() {
        getObject().setY(getObject().getY() + 10);
    }

}
