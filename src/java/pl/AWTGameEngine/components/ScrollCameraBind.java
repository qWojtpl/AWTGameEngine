package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.DefaultComponent;
import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;

@Unique
@DefaultComponent
public class ScrollCameraBind extends ObjectComponent {

    private int maxValue = 100;
    private double wheelSpeed = 0.1;

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
            scrollComponent.setScrollSize(maxValue / 100);
            if(getMouseListener().isMouseWheeled()) {
                int rotation = getMouseListener().getMouseWheelEvent().getWheelRotation();
                if(rotation < 0) {
                    scrollComponent.setValue(scrollComponent.getValue() - wheelSpeed);
                } else {
                    scrollComponent.setValue(scrollComponent.getValue() + wheelSpeed);
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

    @SerializationGetter
    public double getWheelSpeed() {
        return this.wheelSpeed;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    @SerializationSetter
    public void setMaxValue(String maxValue) {
        setMaxValue(Integer.parseInt(maxValue));
    }

    public void setWheelSpeed(double wheelSpeed) {
        this.wheelSpeed = wheelSpeed;
    }

    @SerializationSetter
    public void setWheelSpeed(String wheelSpeed) {
        setWheelSpeed(Double.parseDouble(wheelSpeed));
    }

}
