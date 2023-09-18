package pl.AWTGameEngine.components;

import pl.AWTGameEngine.Main;
import pl.AWTGameEngine.objects.GameObject;

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
        button.setBounds(object.getX(), object.getY(), object.getScaleX(), object.getScaleY());
    }

    @Override
    public void onRemoveComponent() {
        Main.getPanel().remove(button);
    }

    @Override
    public boolean onUpdatePosition(int newX, int newY) {
        button.setBounds(newX, newY, object.getScaleX(), object.getScaleY());
        return true;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
