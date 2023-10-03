package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.components.*;
import pl.AWTGameEngine.objects.GameObject;

import java.util.ArrayList;
import java.util.List;

@Unique
public class Editor extends ObjectComponent {

    private int y = 30;

    public Editor(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        getWindow().setStaticMode(true);
        getCamera().setX(getCamera().getX() - 175);
    }

    @Override
    public void onStaticUpdate() {
        if(getKeyListener().hasPressedKey(37)) {
            getCamera().setX(getCamera().getX() - 8);
        }
        if(getKeyListener().hasPressedKey(39)) {
            getCamera().setX(getCamera().getX() + 8);
        }
        if(getKeyListener().hasPressedKey(38)) {
            getCamera().setY(getCamera().getY() - 8);
        }
        if(getKeyListener().hasPressedKey(40)) {
            getCamera().setY(getCamera().getY() + 8);
        }
    }

    @Override
    public void onCreateGameObject(GameObject newObject) {
        List<ObjectComponent> components = getObject().getComponentsByClass(ListComponent.class);
        if(components.size() == 0) {
            return;
        }
        ListComponent list = ((ListComponent) components.get(0));
        list.setNextItem(newObject.getIdentifier());
    }

    @Override
    public void onContextMenuClick(int option) {
        System.out.println("Option: " + option);
    }

}
