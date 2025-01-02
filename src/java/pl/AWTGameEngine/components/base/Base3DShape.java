package pl.AWTGameEngine.components.base;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Sprite;

public abstract class Base3DShape extends ObjectComponent {

    protected Sprite sprite;
    protected boolean updateSprite = false;

    public Base3DShape(GameObject object) {
        super(object);
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        updateSprite = true;
    }

    @SerializationSetter
    public void setSpriteSource(String source) {
        setSprite(Dependencies.getResourceManager().getResourceAsSprite(source));
    }

}
