package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

@Unique
public class ScrollComponent extends ObjectComponent {

    private final BlankRenderer background = new BlankRenderer(getObject());
    private GameObject scroll;
    private BlankRenderer scrollBackground;
    private boolean dragged;
    private int additionalHeight = 0;
    private boolean highlight = false;

    public ScrollComponent(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        background.setColor(new ColorObject("rgb(175,175,175)"));
        getObject().addComponent(background);
        scroll = getScene().createGameObject("@vo-" + getObject().getIdentifier() + System.nanoTime());
        scrollBackground = new BlankRenderer(scroll);
        scroll.addComponent(scrollBackground);
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
        scrollBackground.getColor().setColor("rgb(150,150,150)");
        if((getMouseListener().getMouseX() >= scroll.getX() && getMouseListener().getMouseX() <= scroll.getX() + scroll.getScaleX()
        && getMouseListener().getMouseY() >= scroll.getY() && getMouseListener().getMouseY() <= scroll.getY() + scroll.getScaleY()) || highlight) {
            scrollBackground.getColor().setColor("rgb(160,160,160)");
            if(getMouseListener().isMousePressed()) {
                System.out.println("highlighted");
                highlight = true;
            }
        }
        updatePosition();
        if(getMouseListener().isMouseDragged() && highlight) {
            additionalHeight = getMouseListener().getMouseY() - getObject().getY();
        }
        if(getMouseListener().isMouseReleased()) {
            highlight = false;
        }
    }

    private void updatePosition() {
        scroll.setX(getObject().getX());
        scroll.setScaleX(getObject().getScaleX());
        scroll.setScaleY(getObject().getScaleY() / 4);
        int y = getObject().getY() + additionalHeight;
        if(y < getObject().getY()) {
            y = getObject().getY();
        }
        if(y > getObject().getY() + getObject().getScaleY() - scroll.getScaleY()) {
            y = getObject().getY() + getObject().getScaleY() - scroll.getScaleY();
        }
        scroll.setY(y);
    }

}
