package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.BindingGetter;
import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.objects.GameObject;

import java.util.List;

public class FlexComponent extends ObjectComponent {

    private int gapX = 7;
    private int gapY = 0;
    private int calculatedHeight = 0;
    private int minimumHeight = 100;

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
        }
        List<GameObject> gameObjects = getObject().getChildren();
        if(gameObjects.size() == 0) {
            return;
        }
        maxWidth += gapX;
        int x = gapX;
        int y = gapY;
        int itemsPerLine = 0;
        boolean checkItemsPerLine = true;
        int i = 0;
        for(GameObject object : gameObjects) {
            if(x > getObject().getSizeX() - maxWidth) {
                x = gapX;
                for(int j = i - 1; j < itemsPerLine + i - 1; j++) {
                    if(gameObjects.size() - 1 < j) {
                        break;
                    }
                    if(gameObjects.get(j).getSizeY() > maxHeight) {
                        maxHeight = gameObjects.get(j).getSizeY();
                    }
                }
                y += maxHeight + gapY;
                maxHeight = 0;
                checkItemsPerLine = false;
            }
            object.setX(getObject().getX() + x);
            object.setY(getObject().getY() + y);
            x += maxWidth;
            if(checkItemsPerLine) {
                itemsPerLine++;
            }
            i++;
        }
        calculatedHeight = getObject().getChildrenHeight() / itemsPerLine + itemsPerLine * gapY;
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

    @BindingGetter
    public int getCalculatedHeight() {
        return Math.max(this.calculatedHeight, minimumHeight);
    }

    @SerializationGetter
    public int getMinimumHeight() {
        return this.minimumHeight;
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

    public void setMinimumHeight(int height) {
        this.minimumHeight = height;
    }

    @SerializationSetter
    public void setMinimumHeight(String height) {
        setMinimumHeight(Integer.parseInt(height));
    }

}