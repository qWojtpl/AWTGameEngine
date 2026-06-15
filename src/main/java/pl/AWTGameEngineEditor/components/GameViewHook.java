package pl.AWTGameEngineEditor.components;

import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.scenes.Scene;

@ComponentGL
@DefaultComponent
public class GameViewHook extends ObjectComponent {

    public GameViewHook(GameObject object) {
        super(object);
    }

    @Override
    public void onMouseClick(int x, int y, int xs, int ys) {
        for(Scene scene : getWindow().getScenes()) {
            if(scene.getName().equals("scenes/editor/editor.xml")) {
                GameView gv = (GameView) scene.getGameObjectByName("game-view").getComponentByClass(GameView.class);
                gv.setFocusMainWindow(false);
                return;
            }
        }
    }

}
