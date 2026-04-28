package pl.AWTGameEngine.components;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.components.management.Unique;
import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.components.base.NetComponent;
import pl.AWTGameEngine.engine.graphics.GraphicsManager;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.NetBlock;
import pl.AWTGameEngine.objects.Sprite;

import java.text.MessageFormat;

@Unique
@DefaultComponent
@WebComponent
public class SpriteRenderer extends NetComponent {

    private Sprite sprite;
    private boolean spriteChanged = false;
    private boolean netSpriteChanged = false;

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
                        .setRotationCenterX(getCamera().parseX(getObject(), getObject().getX()))
                        .setRotationCenterY(getCamera().parseY(getObject(), getObject().getY()))
                        .setContext(getObject())
        );
    }

    @Override
    public void onWebRenderRequest(WebGraphicsManager g) {
        if(sprite != null && spriteChanged) {
            g.execute(MessageFormat.format("drawImage(\"{0}\", \"{1}\");",
                    getObject().getIdentifier(), sprite.getImageBase64(true)));
        }
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public String getSpriteSource() {
        if(sprite == null) {
            return null;
        }
        return this.sprite.getImagePath();
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        spriteChanged = true;
        netSpriteChanged = true;
    }

    @FromXML
    public void setSpriteSource(String spriteSource) {
        setSprite(Dependencies.getResourceManager().getResourceAsSprite(spriteSource));
    }

    @Override
    public void onSynchronizeReceived(String data) {
        setSpriteSource(data);
    }

    @Override
    public NetBlock onSynchronize() {
        netSpriteChanged = false;
        return new NetBlock(getObject().getIdentifier(), getComponentName(), getSpriteSource());
    }

    @Override
    public boolean canSynchronize() {
        return netSpriteChanged;
    }

}
