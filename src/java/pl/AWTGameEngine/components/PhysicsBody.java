package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.GameObject;

public class PhysicsBody extends ObjectComponent {

    public PhysicsBody(GameObject object) {
        super(object);
    }

    @Override
    public void onPreUpdate() {
        object.setY(object.getY() + 10);
    }


}
