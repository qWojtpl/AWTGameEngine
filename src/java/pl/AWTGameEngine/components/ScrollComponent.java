package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.NotOnWeb;
import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

@Unique
@NotOnWeb
public class ScrollComponent extends ObjectComponent {

    private final BlankRenderer background = new BlankRenderer(getObject());
    private GameObject scroll;
    private BlankRenderer scrollRenderer;
    private ColorObject backgroundColor = new ColorObject("rgb(175,175,175)");
    private ColorObject scrollColor = new ColorObject("rgb(150,150,150)");
    private ColorObject selectedScrollColor = new ColorObject("rgb(160,160,160)");
    private double shift = 0;
    private double value = 0;
    private int scrollSize = 2;
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
        if(getMouseListener() == null || scroll == null) {
            return;
        }
        if(getObject().hasComponent(Canvas.class) && !scroll.hasComponent(Canvas.class)) {
            scroll.addComponent(new Canvas(scroll));
        }
        int x = 0, y = 0;
        if(getObject().hasComponent(Canvas.class)) {
            x = getCamera().getX();
            y = getCamera().getY();
        }
        scrollRenderer.setColor(scrollColor);
        if((getMouseListener().getMouseX() - x >= scroll.getX() && getMouseListener().getMouseX() - x <= scroll.getX() + scroll.getSizeX()
        && getMouseListener().getMouseY() - y >= scroll.getY() && getMouseListener().getMouseY() - y <= scroll.getY() + scroll.getSizeY())
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
            if(getObject().hasComponent(ScrollCameraBind.class)) {
                if(!horizontal) {
                    shift -= getCamera().getY();
                } else {
                    shift -= getCamera().getX();
                }
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
            scroll.setSizeX(getObject().getSizeX());
            if(scrollSize == 0) {
                scroll.setSizeY(0);
            } else {
                scroll.setSizeY(getObject().getSizeY() / scrollSize);
            }
            int y = (int) (getObject().getY() + shift);
            value = shift / getObject().getSizeY();
            if(y < getObject().getY()) {
                y = getObject().getY();
                value = 0;
            }
            if(y > getObject().getY() + getObject().getSizeY() - scroll.getSizeY()) {
                y = getObject().getY() + getObject().getSizeY() - scroll.getSizeY();
                value = 1;
            }
            scroll.setY(y);
        } else {
            scroll.setY(getObject().getY());
            if(scrollSize == 0) {
                scroll.setSizeX(0);
            } else {
                scroll.setSizeX(getObject().getSizeX() / scrollSize);
            }
            scroll.setSizeY(getObject().getSizeY());
            int x = (int) (getObject().getX() + shift);
            value = shift / getObject().getSizeX();
            if (x < getObject().getX()) {
                x = getObject().getX();
                value = 0;
            }
            if(x > getObject().getX() + getObject().getSizeX() - scroll.getSizeX()) {
                x = getObject().getX() + getObject().getSizeX() - scroll.getSizeX();
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

    @SerializationGetter
    public int getScrollSize() {
        return this.scrollSize;
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
    public String getBackgroundColor() {
        return this.background.getColorObject().serialize();
    }

    @SerializationGetter
    public String getScrollColor() {
        return this.scrollColor.serialize();
    }

    @SerializationGetter
    public String getSelectedScrollColor() {
        return this.selectedScrollColor.serialize();
    }

    @SerializationGetter
    public boolean isHorizontal() {
        return this.horizontal;
    }

    public void setValue(double value) {
        if(value > 1) {
            value = 1;
        } else if(value < 0) {
            value = 0;
        }
        if(scrollSize == 0) {
            return;
        }
        shift += (value - this.value) * getObject().getSizeX() * (100 - scrollSize);
        this.value = value;
        updatePosition();
    }

    @SerializationSetter
    public void setValue(String value) {
        setValue(Double.parseDouble(value));
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    @SerializationSetter
    public void setHorizontal(String horizontal) {
        setHorizontal(Boolean.parseBoolean(horizontal));
    }

    public void setScrollSize(int size) {
        this.scrollSize = size;
    }

    @SerializationSetter
    public void setScrollSize(String size) {
        setScrollSize(Integer.parseInt(size));
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
