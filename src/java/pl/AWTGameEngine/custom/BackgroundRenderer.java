package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.components.SpriteRenderer;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.scenes.DuplicatedObjectException;
import pl.AWTGameEngine.scenes.SceneLoader;

public class BackgroundRenderer extends ObjectComponent {

    @Override
    public void onAddComponent(GameObject object) {
        SpriteRenderer sprite = new SpriteRenderer();
        sprite.setImage("beaver.jpg");
        for(int x = 0; x < 10; x++) {
            for(int y = 0; y < 30; y++) {
                try {
                    GameObject tile = SceneLoader.getCurrentScene().createGameObject("background-" + x + "x" + y);
                    tile.setX(x * 32);
                    tile.setY(y * 32);
                    tile.setScaleX(32);
                    tile.setScaleY(32);
                    tile.setPriority(0);
                    tile.addComponent(sprite);
                } catch (DuplicatedObjectException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
