package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.components.*;
import pl.AWTGameEngine.components.Canvas;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Editor extends ObjectComponent {

    private int y = 30;

    public Editor(GameObject object) {
        super(object);
        setUnique(true);
    }

    @Override
    public void onAddComponent() {
        getWindow().setStaticMode(true);
        getCamera().setX(getCamera().getX() - 175);
        List<String> identifiers = new ArrayList<>();
        for(GameObject go : getScene().getGameObjects()) {
            identifiers.add(go.getIdentifier());
        }
        List<ObjectComponent> components = getObject().getComponentsByClass(ListComponent.class);
        if(components.size() == 0) {
            return;
        }
        java.awt.List list = ((ListComponent) components.get(0)).getList();
        for(String identifier : identifiers) {
            list.add(identifier);
        }
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

}
