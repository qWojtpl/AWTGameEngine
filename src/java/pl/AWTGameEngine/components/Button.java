package pl.AWTGameEngine.components;

import pl.AWTGameEngine.Main;
import pl.AWTGameEngine.objects.GameObject;

public class Button extends ObjectComponent {

    private String text = "Button";
    private java.awt.Button button;

    @Override
    public void onAddComponent(GameObject object) {
        button = new java.awt.Button(getText());
        Main.getInstance().add(button);
        button.setBounds(object.getX(), object.getY(), object.getScaleX(), object.getScaleY());
    }

    @Override
    public boolean onUpdatePosition(GameObject object, int newX, int newY) {
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
