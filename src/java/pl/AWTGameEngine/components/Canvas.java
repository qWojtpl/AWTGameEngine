package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.DefaultComponent;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.annotations.WebComponent;
import pl.AWTGameEngine.objects.GameObject;

@Unique
@DefaultComponent
@WebComponent
public class Canvas extends ObjectComponent {

    public Canvas(GameObject object) {
        super(object);
    }

    public void alignObject(CanvasAlign canvasAlign) {
        if(CanvasAlign.LEFT.equals(canvasAlign)) {
            getObject().setX(0);
        } else if(CanvasAlign.RIGHT.equals(canvasAlign)) {
            getObject().setX(960 - getObject().getSizeX());
        }
    }

    @SerializationSetter
    public void setAlign(String align) {
        try {
            CanvasAlign canvasAlign = CanvasAlign.valueOf(align.toUpperCase());
            alignObject(canvasAlign);
        } catch(IllegalArgumentException e) {
            alignObject(CanvasAlign.LEFT);
        }
    }

    public void alignObject(CanvasAlign[] aligns) {
        for(CanvasAlign align : aligns) {
            alignObject(align);
        }
    }

    public enum CanvasAlign {

        LEFT,
        RIGHT

    }

}
