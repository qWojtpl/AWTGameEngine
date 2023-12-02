package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.objects.GameObject;

@Unique
public class Canvas extends ObjectComponent {

    public Canvas(GameObject object) {
        super(object);
    }

    public void alignObject(CanvasAlign canvasAlign) {
        if(CanvasAlign.LEFT.equals(canvasAlign)) {
            getObject().setXForced(0);
        } else if(CanvasAlign.RIGHT.equals(canvasAlign)) {
            getObject().setXForced((int) (480 * (getWindow().getPanel().getMultiplier() - 1) - getObject().getScaleX()));
        }
    }

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
