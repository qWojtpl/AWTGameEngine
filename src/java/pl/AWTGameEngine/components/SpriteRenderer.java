package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.engine.GraphicsManager;
import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Sprite;

import java.awt.*;
import java.awt.geom.AffineTransform;

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
        Graphics2D g2d = (Graphics2D) g.getGraphics();
        AffineTransform oldTransform = g2d.getTransform();
        if(getObject().getRotation() != 0) {
            AffineTransform transform = new AffineTransform();
            transform.rotate(Math.toRadians(getObject().getRotation()),
                    getCamera().parseX(getObject(), getObject().getCenterX()),
                    getCamera().parseY(getObject(), getObject().getCenterY()));
            g2d.transform(transform);
        }
        g.drawImage(sprite.getImage(),
                getCamera().parseX(getObject(), getObject().getX()),
                getCamera().parseY(getObject(), getObject().getY()),
                getCamera().parseScale(getObject().getSizeX()),
                getCamera().parseScale(getObject().getSizeY()));
        g2d.setTransform(oldTransform);
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
