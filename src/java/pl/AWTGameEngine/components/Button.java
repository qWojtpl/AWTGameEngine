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
    }

    @Override
    public void onAddComponent() {
        button = new java.awt.Button(getText());
        Main.getPanel().add(button);
        button.setLabel(getText());
        button.setFocusable(false);
        button.setLocation(object.getX(), object.getY());
        button.setSize(object.getScaleX(), object.getScaleY());
    }

    @Override
    public void onRemoveComponent() {
        Main.getPanel().remove(button);
    }

    @Override
    public void onRender(Graphics g) {
        button.setLocation(object.getX() - Camera.getRelativeX(object), object.getY() - Camera.getRelativeY(object));
        button.setSize(object.getScaleX(), object.getScaleY());
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
