package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.annotations.components.management.Unique;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.base.ObjectComponent;
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

    @FromXML
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
