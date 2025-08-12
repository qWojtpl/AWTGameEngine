package pl.AWTGameEngine.components;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.*;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.graphics.GraphicsManager;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.engine.graphics.WebRenderable;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Sprite;

import java.text.MessageFormat;

@Unique
@DefaultComponent
@WebComponent
public class SpriteRenderer extends ObjectComponent implements WebRenderable {

    private Sprite sprite;
    private boolean propertyChanged = false;

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
                getCamera().parseX(getObject(), getObject().getX()),
                getCamera().parseY(getObject(), getObject().getY()),
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
        if(propertyChanged && sprite != null) {
            g.execute(MessageFormat.format("drawImage(\"{0}\", \"{1}\");",
                    getObject().getIdentifier(), sprite.getImageBase64()));
        }
        propertyChanged = false;
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
        propertyChanged = true;
    }

    @SerializationSetter
    public void setSpriteSource(String spriteSource) {
        setSprite(Dependencies.getResourceManager().getResourceAsSprite(spriteSource));
    }

}
