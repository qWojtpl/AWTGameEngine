package pl.AWTGameEngine.components;

import pl.AWTGameEngine.Main;
import pl.AWTGameEngine.objects.Camera;
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
        object.getScene().getWindow().getPanel().add(button);
        button.setLabel(getText());
        button.setFocusable(false);
        button.setLocation(object.getX(), object.getY());
        button.setSize(object.getScaleX(), object.getScaleY());
    }

    @Override
    public void onRemoveComponent() {
        object.getScene().getWindow().getPanel().remove(button);
    }

    @Override
    public void onRender(Graphics g) {
        button.setLocation((int) (object.getX() - getCamera().getRelativeX(object) * getCamera().getZoom()),
                (int) ((object.getY() - getCamera().getRelativeY(object)) * getCamera().getZoom()));
        button.setSize((int) (object.getScaleX() * getCamera().getZoom()), (int) (object.getScaleY() * getCamera().getZoom()));
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
