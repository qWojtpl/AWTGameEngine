package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.annotations.DefaultComponent;
import pl.AWTGameEngine.annotations.WebComponent;
import pl.AWTGameEngine.components.SpriteRenderer;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.graphics.GraphicsManager;
import pl.AWTGameEngine.engine.helpers.ScreenShooter;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Sprite;

@DefaultComponent
@WebComponent
public class Filter extends ObjectComponent {

    private SpriteRenderer spriteRenderer;

    public Filter(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
         spriteRenderer = ((SpriteRenderer) getObject().getComponentByClass(SpriteRenderer.class));
    }

    @Override
    public void onPreRender(GraphicsManager g) {
        Sprite sprite = spriteRenderer.getSprite();
        if(sprite == null) {
            sprite = ScreenShooter.takeScreenshot(getWindow().getCurrentScene().getPanel());
            spriteRenderer.setSprite(sprite);
        } else {
            sprite.updateBufferedImage(ScreenShooter.createImage(getWindow().getCurrentScene().getPanel()));
        }
        sprite.toNegative().scale(0.2);
    }

}
