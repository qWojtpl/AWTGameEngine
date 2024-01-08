package pl.AWTGameEngine.components;

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
            if(scrollComponent.isHorizontal()) {
                getCamera().setX((int) (scrollComponent.getValue() * maxValue));
            } else {
                getCamera().setY((int) (scrollComponent.getValue() * maxValue));
            }
        }
    }

}
