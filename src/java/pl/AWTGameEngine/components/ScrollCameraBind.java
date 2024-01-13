package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.objects.GameObject;

public class ScrollCameraBind extends ObjectComponent {

    private int maxValue = 100;

    public ScrollCameraBind(GameObject object) {
        super(object);
    }

    @Override
    public void onUpdate() {
        onStaticUpdate();
    }

    @Override
    public void onStaticUpdate() {
        for(ObjectComponent component : getObject().getComponentsByClass(ScrollComponent.class)) {
            ScrollComponent scrollComponent = (ScrollComponent) component;
            if(getMouseListener().isMouseWheeled()) {
                int rotation = getMouseListener().getMouseWheelEvent().getWheelRotation();
                if(rotation < 0) {
                    scrollComponent.setValue(scrollComponent.getValue() - 0.1);
                } else {
                    scrollComponent.setValue(scrollComponent.getValue() + 0.1);
                }
            }
            if(scrollComponent.isHorizontal()) {
                getCamera().setX((int) (scrollComponent.getValue() * maxValue));
            } else {
                getCamera().setY((int) (scrollComponent.getValue() * maxValue));
            }
        }
    }

    @SerializationGetter
    public int getMaxValue() {
        return this.maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    @SerializationSetter
    public void setMaxValue(String maxValue) {
        setMaxValue(Integer.parseInt(maxValue));
    }

}
