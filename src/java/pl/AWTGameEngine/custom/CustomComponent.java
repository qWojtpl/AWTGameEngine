package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

public class CustomComponent extends ObjectComponent {

    private int a = 1;

    @Override
    public void onRender(GameObject object, Graphics g) {
        if(a % 10 == 0) {
            object.setX(object.getX() + 1);
            object.setY(object.getY() + 1);
        }
        a++;
    }

}
