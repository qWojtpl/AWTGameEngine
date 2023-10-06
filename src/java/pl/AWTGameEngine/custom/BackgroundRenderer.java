package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.components.Animator;
import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.components.SpriteRenderer;
import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.objects.GameObject;



public class BackgroundRenderer extends ObjectComponent {

    public BackgroundRenderer(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        for(int x = 0; x < 10; x++) {
            for(int y = 0; y < 33; y++) {
                GameObject tile = getScene().createGameObject("background-" + x + "x" + y);
                tile.setX(x * 32);
                tile.setY(y * 32);
                tile.setScaleX(32);
                tile.setScaleY(32);
                tile.setPriority(0);
                tile.setParent(getObject().getParent());
                SpriteRenderer s = new SpriteRenderer(tile);
                s.setImage(ResourceManager.getResourceAsImage("beaver.jpg"));
                tile.addComponent(s);
            }
        }
    }

}
