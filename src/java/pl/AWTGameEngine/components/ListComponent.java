package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

public class ListComponent extends ObjectComponent {

    private List list;

    public ListComponent(GameObject object) {
        super(object);
        setUnique(true);
    }

    @Override
    public void onAddComponent() {
        list = new List();
        list.setFocusable(false);
        list.setLocation(getObject().getX(), getObject().getY());
        list.setSize(getObject().getScaleX(), getObject().getScaleY());
        list.setFont(getWindow().getFont().deriveFont(24f));
        getWindow().getPanel().add(list);
    }

    @Override
    public void onRemoveComponent() {
        getWindow().getPanel().remove(list);
    }

    @Override
    public void onRender(Graphics g) {
        if(list == null) {
            return;
        }
        list.setLocation((int) ((getObject().getX() - getCamera().getRelativeX(getObject())) * getCamera().getZoom()),
                (int) ((getObject().getY() - getCamera().getRelativeY(getObject())) * getCamera().getZoom()));
        list.setSize((int) (getObject().getScaleX() * getCamera().getZoom()),
                (int) (getObject().getScaleY() * getCamera().getZoom()));
    }

    public void setNextItem(String item) {
        list.add(item);
    }

}
