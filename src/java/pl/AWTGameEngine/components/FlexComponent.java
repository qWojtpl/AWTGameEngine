package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.objects.GameObject;

public class FlexComponent extends ObjectComponent {

    private int gapX = 7;
    private int gapY = 0;
    private int calculatedHeight = 0;

    public FlexComponent(GameObject object) {
        super(object);
    }

    @Override
    public void onPreUpdate() {
        double maxWidth = 0;
        double maxHeight = 0;
        for(GameObject object : getObject().getChildren()) {
            if(object.getSizeX() > maxWidth) {
                maxWidth = object.getSizeX();
            }
            if(object.getSizeY() > maxHeight) {
                maxHeight = object.getSizeY();
            }
        }
        maxWidth += gapX;
        maxHeight += gapY;
        int x = gapX;
        int y = gapY;
        for(GameObject object : getObject().getChildren()) {
            if(x > getObject().getSizeX() - maxHeight) {
                x = gapX;
                y += maxHeight;
            }
            object.setX(getObject().getX() + x);
            object.setY(getObject().getY() + y);
            x += maxWidth;
        }
        calculatedHeight = y;
    }

    @Override
    public void onStaticUpdate() {
        onPreUpdate();
    }

    @SerializationGetter
    public int getGapX() {
        return this.gapX;
    }

    @SerializationGetter
    public int getGapY() {
        return this.gapY;
    }

    public int getCalculatedHeight() {
        return this.calculatedHeight;
    }

    public void setGapX(int gapX) {
        this.gapX = gapX;
    }

    @SerializationSetter
    public void setGapX(String gapX) {
        setGapX(Integer.parseInt(gapX));
    }

    public void setGapY(int gapY) {
        this.gapY = gapY;
    }

    @SerializationSetter
    public void setGapY(String gapY) {
        setGapY(Integer.parseInt(gapY));
    }

}