package pl.AWTGameEngine.components;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.Component3D;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.graphics.Renderable3D;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Sprite;

@Component3D
public class Sphere3D extends ObjectComponent implements Renderable3D {

    private Sprite sprite;
    private boolean updateSprite = false;

    public Sphere3D(GameObject object) {
        super(object);
    }

    @Override
    public void on3DRenderRequest(GraphicsManager3D g) {
        Sprite newSprite = null;
        if(updateSprite) {
            newSprite = sprite;
            updateSprite = false;
        }
        g.renderSphere(getObject().getIdentifier(), getObject().getPosition(), getObject().getSize(), getObject().getRotation(), newSprite);
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
