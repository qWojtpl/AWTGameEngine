package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.engine.GraphicsManager;
import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Sprite;

@Unique
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
                sprite.getImage(),
                getCamera().parseX(getObject(), getObject().getX()),
                getCamera().parseY(getObject(), getObject().getY()),
                getCamera().parseScale(getObject().getSizeX()),
                getCamera().parseScale(getObject().getSizeY()),
                new GraphicsManager.RenderOptions()
                        .setRotation(getObject().getRotation())
                        .setRotationCenterX(getCamera().parseX(getObject(), getObject().getCenterX()))
                        .setRotationCenterY(getCamera().parseY(getObject(), getObject().getCenterY()))
        );
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

    @SerializationSetter
    public void setSpriteSource(String spriteSource) {
        setSprite(ResourceManager.getResourceAsSprite(spriteSource));
    }

}
