package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.engine.graphics.GraphicsManager;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.engine.graphics.WebRenderable;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

public class Border extends ObjectComponent implements WebRenderable {

    private boolean enabled = true;
    private ColorObject color = new ColorObject();
    private boolean propertyChanged = true;

    public Border(GameObject object) {
        super(object);
    }

    @Override
    public void onAfterRender(GraphicsManager g) {
        if(!isEnabled()) {
            return;
        }
        g.drawRect(
                getCamera().parseX(getObject(), getObject().getX()),
                getCamera().parseY(getObject(), getObject().getY()),
                getCamera().parseScale(getObject().getSizeX()),
                getCamera().parseScale(getObject().getSizeY()),
                new GraphicsManager.RenderOptions()
                        .setColor(color.getColor())
                        .setRotation(getObject().getRotationX())
                        .setRotationCenterX(getCamera().parseX(getObject(), getObject().getCenterX()))
                        .setRotationCenterY(getCamera().parseY(getObject(), getObject().getCenterY()))
        );
    }

    @Override
    public void onWebRenderRequest(WebGraphicsManager g) {
        if(!propertyChanged || !enabled) {
            return;
        }
        g.execute(String.format("setBorder(\"%s\", \"%s\")",
                getObject().getIdentifier(),
                color.serialize()));
    }


    @SerializationGetter
    public boolean isEnabled() {
        return this.enabled;
    }

    public ColorObject getColorObject() {
        return this.color;
    }

    @SerializationGetter
    public String getColor() {
        return this.color.serialize();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        propertyChanged = true;
    }

    @SerializationSetter
    public void setEnabled(String enabled) {
        setEnabled(Boolean.parseBoolean(enabled));
    }

    public void setColor(ColorObject color) {
        if(color == null) {
            return;
        }
        this.color = color;
        propertyChanged = true;
    }

    @SerializationSetter
    public void setColor(String color) {
        setColor(new ColorObject(color));
    }

}
