package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

public class Button extends ObjectComponent {

    private String text = "Button";
    private java.awt.Button button;

    public Button(GameObject object) {
        super(object);
        setUnique(true);
    }

    @Override
    public void onAddComponent() {
        button = new java.awt.Button(getText());
        getScene().getWindow().getPanel().add(button);
        button.setLabel(getText());
        button.setFocusable(false);
    }

    @Override
    public void onRemoveComponent() {
        getScene().getWindow().getPanel().remove(button);
    }

    @Override
    public void onRender(Graphics g) {
        button.setLocation((int) ((getObject().getX() - getCamera().getRelativeX(getObject())) * getCamera().getZoom()),
                (int) ((getObject().getY() - getCamera().getRelativeY(getObject())) * getCamera().getZoom()));
        button.setSize((int) (getObject().getScaleX() * getCamera().getZoom()),
                (int) (getObject().getScaleY() * getCamera().getZoom()));
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
