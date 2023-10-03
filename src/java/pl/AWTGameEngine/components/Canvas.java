package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.objects.GameObject;

@Unique
public class Canvas extends ObjectComponent {

    public Canvas(GameObject object) {
        super(object);
    }

}
