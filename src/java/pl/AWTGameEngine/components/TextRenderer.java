package pl.AWTGameEngine.components;

import pl.AWTGameEngine.Main;
import pl.AWTGameEngine.engine.KeyListener;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

public class TextRenderer extends ObjectComponent {

    private String text = "";
    private Color color = Color.BLACK;

    @Override
    public void onRender(GameObject object) {
        setText("key: " + KeyListener.getLastPress());
        Graphics2D g = (Graphics2D) Main.getInstance().getGraphics();
        g.setFont(g.getFont().deriveFont(30f));
        g.drawString(getText(), object.getX(), object.getY());
    }

    public String getText() {
        return this.text;
    }

    public Color getColor() {
        return this.color;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}
