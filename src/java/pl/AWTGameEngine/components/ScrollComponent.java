package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

@Unique
public class ScrollComponent extends ObjectComponent {

    private final BlankRenderer background = new BlankRenderer(getObject());
    private GameObject scroll;
    private BlankRenderer scrollRenderer;
    private ColorObject backgroundColor = new ColorObject("rgb(175,175,175)");
    private ColorObject scrollColor = new ColorObject("rgb(150,150,150)");
    private ColorObject selectedScrollColor = new ColorObject("rgb(160,160,160)");
    private int shift = 0;
    private double value = 0;
    private boolean selected = false;
    private boolean horizontal = false;

    public ScrollComponent(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        background.setColor(backgroundColor);
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
        && getMouseListener().getMouseY() >= scroll.getY() && getMouseListener().getMouseY() <= scroll.getY() + scroll.getScaleY())
        || selected) {
            scrollRenderer.setColor(selectedScrollColor);
            if(getMouseListener().isMousePressed()) {
                selected = true;
            }
        }
        if(getMouseListener().isMouseDragged() && selected) {
            if(!horizontal) {
                shift = getMouseListener().getMouseY() - getObject().getY();
            } else {
                shift = getMouseListener().getMouseX() - getObject().getX();
            }
        }
        if(getMouseListener().isMouseReleased()) {
            selected = false;
        }
        updatePosition();
    }

    private void updatePosition() {
        if(!horizontal) {
            scroll.setX(getObject().getX());
            scroll.setScaleX(getObject().getScaleX());
            scroll.setScaleY(getObject().getScaleY() / 4);
            int y = getObject().getY() + shift;
            value = (double) shift / getObject().getScaleY();
            if(y < getObject().getY()) {
                y = getObject().getY();
                value = 0;
            }
            if(y > getObject().getY() + getObject().getScaleY() - scroll.getScaleY()) {
                y = getObject().getY() + getObject().getScaleY() - scroll.getScaleY();
                value = 1;
            }
            scroll.setY(y);
        } else {
            scroll.setY(getObject().getY());
            scroll.setScaleX(getObject().getScaleX() / 4);
            scroll.setScaleY(getObject().getScaleY());
            int x = getObject().getX() + shift;
            value = (double) shift / getObject().getScaleX();
            if (x < getObject().getX()) {
                x = getObject().getX();
                value = 0;
            }
            if(x > getObject().getX() + getObject().getScaleX() - scroll.getScaleX()) {
                x = getObject().getX() + getObject().getScaleX() - scroll.getScaleX();
                value = 1;
            }
            scroll.setX(x);
        }
    }

    public double getValue() {
        return this.value;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public ColorObject getBackgroundColorObject() {
        return this.background.getColorObject();
    }

    public ColorObject getScrollColorObject() {
        return this.scrollColor;
    }

    public ColorObject getSelectedScrollColorObject() {
        return this.selectedScrollColor;
    }

    @SerializationGetter
    public ColorObject getBackgroundColor() {
        return this.background.getColorObject();
    }

    @SerializationGetter
    public ColorObject getScrollColor() {
        return this.scrollColor;
    }

    @SerializationGetter
    public ColorObject getSelectedScrollColor() {
        return this.selectedScrollColor;
    }

    @SerializationGetter
    public boolean isHorizontal() {
        return this.horizontal;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    @SerializationSetter
    public void setHorizontal(String horizontal) {
        setHorizontal(Boolean.parseBoolean(horizontal));
    }

    public void setBackgroundColor(ColorObject object) {
        backgroundColor = object;
    }

    @SerializationSetter
    public void setBackgroundColor(String color) {
        setBackgroundColor(new ColorObject(color));
    }

    public void setScrollColor(ColorObject object) {
        scrollColor = object;
    }

    @SerializationSetter
    public void setScrollColor(String color) {
        setScrollColor(new ColorObject(color));
    }

    public void setSelectedScrollColor(ColorObject object) {
        selectedScrollColor = object;
    }

    @SerializationSetter
    public void setSelectedScrollColor(String color) {
        setSelectedScrollColor(new ColorObject(color));
    }

}
