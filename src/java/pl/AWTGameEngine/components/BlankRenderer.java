package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.engine.graphics.GraphicsManager;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.engine.graphics.WebRenderable;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

import java.text.MessageFormat;

public class BlankRenderer extends ObjectComponent implements WebRenderable {

    private ColorObject color = new ColorObject();
    private boolean changedColor = false;

    public BlankRenderer(GameObject object) {
        super(object);
    }

    @Override
    public void onRender(GraphicsManager g) {
        g.fillRect(
                getCamera().parseX(getObject(), getObject().getX()),
                getCamera().parseY(getObject(), getObject().getY()),
                getCamera().parseScale(getObject().getSizeX()),
                getCamera().parseScale(getObject().getSizeY()),
                new GraphicsManager.RenderOptions()
                        .setColor(color.getColor())
                        .setRotation(getObject().getRotation())
                        .setRotationCenterX(getCamera().parseX(getObject(), getObject().getCenterX()))
                        .setRotationCenterY(getCamera().parseY(getObject(), getObject().getCenterY()))
        );
    }

    public ColorObject getColorObject() {
        return this.color;
    }

    @SerializationGetter
    public String getColor() {
        return this.color.serialize();
    }

    public void setColor(ColorObject color) {
        if(color == null) {
            return;
        }
        this.color = color;
        changedColor = true;
    }

    @SerializationSetter
    public void setColor(String color) {
        this.color.setColor(color);
        changedColor = true;
    }

    @Override
    public void onWebRenderRequest(WebGraphicsManager g) {
        if(changedColor) {
            g.execute(MessageFormat.format("setColor(\"{0}\", \"{1}\");",
                    getObject().getIdentifier(), this.color.serialize()));
        }
        changedColor = false;
    }

}
