package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.RemoveOnCompile;
import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.lang.reflect.Field;

@Unique
@RemoveOnCompile
public class Border extends ObjectComponent {

    private boolean enabled = true;
    private Color color = Color.BLACK;

    public Border(GameObject object) {
        super(object);
    }

    @Override
    public void onRender(Graphics g) {
        if(!isEnabled()) {
            return;
        }
        g.setColor(getColor());
        g.drawRect((int) ((getObject().getX() - getCamera().getRelativeX(getObject())) * getCamera().getZoom()),
                (int) ((getObject().getY() - getCamera().getRelativeY(getObject())) * getCamera().getZoom()),
                (int) ((getObject().getScaleX()) * getCamera().getZoom()),
                (int) ((getObject().getScaleY()) * getCamera().getZoom()));
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public Color getColor() {
        return this.color;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setEnabled(String enabled) {
        setEnabled(Boolean.parseBoolean(enabled));
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setColor(String color) {
        Color c;
        try {
            Field field = Class.forName("java.awt.Color").getField(color.toLowerCase());
            c = (Color) field.get(null);
        } catch (Exception e) {
            c = Color.BLACK;
        }
        setColor(c);
    }

}
