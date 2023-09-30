package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.components.BlankRenderer;
import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.components.Canvas;
import pl.AWTGameEngine.components.TextRenderer;
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
        for(String identifier : identifiers) {
            GameObject newGo = getScene().createGameObject("editor-" + identifier);
            y += 20;
            newGo.setX(25);
            newGo.setY(y);
            newGo.setScaleX(125);
            newGo.setScaleY(18);
            newGo.setPriority(1100);
            newGo.addComponent(new BlankRenderer(newGo));
            newGo.addComponent(new Canvas(newGo));
            TextRenderer text = new TextRenderer(newGo);
            text.setText(identifier);
            text.setColor(Color.WHITE);
            text.setSize(13);
            text.setX(10);
            newGo.addComponent(text);
        }
    }

    @Override
    public void onRender(Graphics g) {
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