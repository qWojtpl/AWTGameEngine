package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.engine.GraphicsManager;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

public class LineComponent extends ObjectComponent {

    private ColorObject color = new ColorObject(Color.BLACK);
    private float thickness = 3;

    public LineComponent(GameObject object) {
        super(object);
    }

    @Override
    public void onRender(GraphicsManager g) {
        g.drawLine(
                getCamera().parseX(getObject(), getObject().getX()),
                getCamera().parseY(getObject(), getObject().getY()),
                getCamera().parseX(getObject(), getObject().getX() + getObject().getSizeX()),
                getCamera().parseY(getObject(), getObject().getY() + getObject().getSizeY()),
                new GraphicsManager.RenderOptions()
                        .setColor(color.getColor())
                        .setStroke(thickness)
        );
    }

    public ColorObject getColorObject() {
        return this.color;
    }

    @SerializationGetter
    public String getColor() {
        return this.color.serialize();
    }

    @SerializationGetter
    public float getThickness() {
        return this.thickness;
    }

    public void setColorObject(ColorObject color) {
        this.color = color;
    }

    @SerializationSetter
    public void setColor(String color) {
        setColorObject(new ColorObject(color));
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
    }

    @SerializationSetter
    public void setThickness(String thickness) {
        setThickness(Float.parseFloat(thickness));
    }

}
