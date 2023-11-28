package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

@Unique
public class ScrollComponent extends ObjectComponent {

    private final BlankRenderer background = new BlankRenderer(getObject());
    private GameObject scroll;
    private BlankRenderer scrollRenderer;
    private ColorObject scrollColor = new ColorObject("rgb(150,150,150)");
    private ColorObject selectedScrollColor = new ColorObject("rgb(160,160,160)");
    private int additionalHeight = 0;
    private double value = 0;
    private boolean selected = false;

    public ScrollComponent(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        background.setColor(new ColorObject("rgb(175,175,175)"));
        getObject().addComponent(background);
        scroll = getScene().createGameObject("@vo-" + getObject().getIdentifier() + System.nanoTime());
        scrollRenderer = new BlankRenderer(scroll);
        scroll.addComponent(scrollRenderer);
        updatePosition();
    }

    @Override
    public void onRemoveComponent() {
        getObject().removeComponent(background);
        getScene().removeGameObject(scroll);
        scroll = null;
    }

    @Override
    public void onStaticUpdate() {
        onUpdate();
    }

    @Override
    public void onUpdate() {
        scrollRenderer.setColor(scrollColor);
        if((getMouseListener().getMouseX() >= scroll.getX() && getMouseListener().getMouseX() <= scroll.getX() + scroll.getScaleX()
        && getMouseListener().getMouseY() >= scroll.getY() && getMouseListener().getMouseY() <= scroll.getY() + scroll.getScaleY()) || selected) {
            scrollRenderer.setColor(selectedScrollColor);
            if(getMouseListener().isMousePressed()) {
                selected = true;
            }
        }
        if(getMouseListener().isMouseDragged() && selected) {
            additionalHeight = getMouseListener().getMouseY() - getObject().getY();
        }
        if(getMouseListener().isMouseReleased()) {
            selected = false;
        }
        updatePosition();
    }

    private void updatePosition() {
        scroll.setX(getObject().getX());
        scroll.setScaleX(getObject().getScaleX());
        scroll.setScaleY(getObject().getScaleY() / 4);
        int y = getObject().getY() + additionalHeight;
        value = (double) additionalHeight / getObject().getScaleY();
        if(y < getObject().getY()) {
            y = getObject().getY();
            value = 0;
        }
        if(y > getObject().getY() + getObject().getScaleY() - scroll.getScaleY()) {
            y = getObject().getY() + getObject().getScaleY() - scroll.getScaleY();
            value = 1;
        }
        scroll.setY(y);
    }

    public double getValue() {
        return this.value;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public ColorObject getBackgroundColor() {
        return this.background.getColor();
    }

    public ColorObject getScrollColor() {
        return this.scrollColor;
    }

    public ColorObject getSelectedScrollColor() {
        return this.selectedScrollColor;
    }

    public void setBackgroundColor(ColorObject object) {
        background.setColor(object);
    }

    public void setBackgroundColor(String color) {
        background.getColor().setColor(color);
    }

    public void setScrollColor(ColorObject object) {
        scrollColor = object;
    }

    public void setScrollColor(String color) {
        scrollColor.setColor(color);
    }

    public void setSelectedScrollColor(ColorObject object) {
        selectedScrollColor = object;
    }

    public void setSelectedScrollColor(String color) {
        selectedScrollColor.setColor(color);
    }

}
