package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

public class LineComponent extends ObjectComponent {

    private ColorObject color = new ColorObject(Color.BLACK);

    public LineComponent(GameObject object) {
        super(object);
    }

    @Override
    public void onRender(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(color.getColor());
        g2d.drawLine(
                getCamera().parseX(getObject(), getObject().getX()),
                getCamera().parseY(getObject(), getObject().getY()),
                getCamera().parseX(getObject(), getObject().getX() + getObject().getSizeX()),
                getCamera().parseY(getObject(), getObject().getY() + getObject().getSizeY())
        );
        g2d.setStroke(new BasicStroke(1));
    }

    public ColorObject getColorObject() {
        return this.color;
    }

    @SerializationGetter
    public String getColor() {
        return this.color.serialize();
    }

    public void setColorObject(ColorObject color) {
        this.color = color;
    }

    @SerializationSetter
    public void setColor(String color) {
        setColorObject(new ColorObject(color));
    }

}
