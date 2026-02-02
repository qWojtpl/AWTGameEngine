package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.SpriteRenderer;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.graphics.GraphicsManager;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
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
        computeFilter();
    }

    @Override
    public void onWebRenderRequest(WebGraphicsManager g) {
        computeFilter();
    }

    private void computeFilter() {
        Sprite sprite = spriteRenderer.getSprite();
        if(sprite == null) {
            sprite = ScreenShooter.takeScreenshot(getWindow().getCurrentScene().getPanel());
            spriteRenderer.setSprite(sprite);
        } else {
            sprite.updateBufferedImage(ScreenShooter.createImage(getWindow().getCurrentScene().getPanel()));
        }
        sprite.scale(0.9);
    }

}
