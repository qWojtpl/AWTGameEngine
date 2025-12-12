package pl.AWTGameEngine.components;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.*;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.graphics.GraphicsManager;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Sprite;

import java.text.MessageFormat;

@Unique
@DefaultComponent
@WebComponent
public class SpriteRenderer extends ObjectComponent {

    private Sprite sprite;

    public SpriteRenderer(GameObject object) {
        super(object);
    }

    @Override
    public void onRender(GraphicsManager g) {
        if(sprite == null) {
            return;
        }
        g.drawImage(
                sprite,
                getCamera().parseX(getObject(), getObject().getX() - getObject().getSizeX() / 2),
                getCamera().parseY(getObject(), getObject().getY() - getObject().getSizeY() / 2),
                getCamera().parsePlainValue(getObject().getSizeX()),
                getCamera().parsePlainValue(getObject().getSizeY()),
                new GraphicsManager.RenderOptions()
                        .setRotation(getObject().getRotation().getX())
                        .setRotationCenterX(getCamera().parseX(getObject(), getObject().getCenterX()))
                        .setRotationCenterY(getCamera().parseY(getObject(), getObject().getCenterY()))
                        .setContext(getObject())
        );
    }

    @Override
    public void onWebRenderRequest(WebGraphicsManager g) {
        if(sprite != null) {
            g.execute(MessageFormat.format("drawImage(\"{0}\", \"{1}\");",
                    getObject().getIdentifier(), sprite.getImageBase64(true)));
        }
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    @SerializationGetter
    public String getSpriteSource() {
        return getSprite().getImagePath();
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    @FromXML
    public void setSpriteSource(String spriteSource) {
        setSprite(Dependencies.getResourceManager().getResourceAsSprite(spriteSource));
    }

}
