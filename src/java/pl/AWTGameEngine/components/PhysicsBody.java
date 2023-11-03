package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Vector;

@Unique
public class PhysicsBody extends ObjectComponent {

    private double forceX = 0;

    public PhysicsBody(GameObject object) {
        super(object);
    }

    @Override
    public void onStaticUpdate() {
        onUpdate();
    }

    @Override
    public void onUpdate() {
        if(forceX == 0) {
            return;
        }

        if(forceX > 0) {
            if(!getObject().setX(getObject().getX() + 1)) {
                forceX = (int) (forceX / 2);
                forceX = -forceX;
            } else {
                forceX -= 1;
            }
        } else if(forceX < 0) {
            if(!getObject().setX(getObject().getX() - 1)) {
                forceX = (int) (forceX / 2);
                forceX = -forceX;
            } else {
                forceX += 1;
            }
        }
    }

    public void push(double force, Vector vector) {
        forceX += vector.getX() * force;
    }

}
