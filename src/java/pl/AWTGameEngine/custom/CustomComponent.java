package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;

public class CustomComponent extends ObjectComponent {

    private int a = 0;

    @Override
    public void onRender(GameObject object) {
        if(a % 10 == 0) {
            object.setX(object.getX() + 1);
            object.setY(object.getY() + 1);
        }
        a++;
    }

}
