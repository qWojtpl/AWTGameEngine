package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.DefaultComponent;
import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.FromXML;
import pl.AWTGameEngine.annotations.WebComponent;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.graphics.GraphicsManager;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.NetBlock;

import java.text.MessageFormat;

@DefaultComponent
@WebComponent
public class BlankRenderer extends ObjectComponent {

    private ColorObject color = new ColorObject();
    private boolean changedColor = false;
    private boolean netColorChanged = false;

    public BlankRenderer(GameObject object) {
        super(object);
    }

    @Override
    public void onRender(GraphicsManager g) {
        g.fillRect(
                getCamera().parseX(getObject(), getObject().getX() - getObject().getSizeX() / 2),
                getCamera().parseY(getObject(), getObject().getY() - getObject().getSizeY() / 2),
                getCamera().parsePlainValue(getObject().getSizeX()),
                getCamera().parsePlainValue(getObject().getSizeY()),
                new GraphicsManager.RenderOptions()
                        .setColor(color.getColor())
                        .setRotation(getObject().getRotation().getX())
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
        if(this.color.equals(color)) {
            return;
        }
        this.color = color;
        changedColor = true;
        netColorChanged = true;
    }

    @FromXML
    public void setColor(String color) {
        String oldColor = this.color.serialize();
        this.color.setColor(color);
        String newColor = this.color.serialize();
        if(oldColor.equals(newColor)) {
            return;
        }
        changedColor = true;
        netColorChanged = true;
    }

    @Override
    public void onWebRenderRequest(WebGraphicsManager g) {
        if(changedColor) {
            g.execute(MessageFormat.format("setColor(\"{0}\", \"{1}\");",
                    getObject().getIdentifier(), this.color.serialize()));
            changedColor = false;
        }
    }

    // Net

    @Override
    public boolean canSynchronize() {
        //todo: bugged
        return true;
//        if(netColorChanged) {
//            netColorChanged = false;
//            return true;
//        }
//        return false;
    }

    @Override
    public NetBlock onSynchronize() {
        return new NetBlock(
                getObject().getIdentifier(),
                this.getClass().getName(),
                getColor()
        );
    }

    @Override
    public void onSynchronizeReceived(String data) {
        setColor(data);
    }

    @Override
    public void clearNetCache() {
        netColorChanged = true;
    }

}
