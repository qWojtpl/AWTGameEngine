package pl.AWTGameEngine.components;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.graphics.GraphicsManagerGL;
import pl.AWTGameEngine.engine.panels.PanelGL;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.render.Sprite;

import java.util.List;

@ComponentGL
public class Skybox extends ObjectComponent {

    private GraphicsManagerGL graphicsManagerGL;
    private final List<Sprite> sprites = List.of(
            Dependencies.getResourceManager().getResourceAsSprite("hdr_sprites/skybox/right.jpg"),
            Dependencies.getResourceManager().getResourceAsSprite("hdr_sprites/skybox/left.jpg"),
            Dependencies.getResourceManager().getResourceAsSprite("hdr_sprites/skybox/top.jpg"),
            Dependencies.getResourceManager().getResourceAsSprite("hdr_sprites/skybox/bottom.jpg"),
            Dependencies.getResourceManager().getResourceAsSprite("hdr_sprites/skybox/front.jpg"),
            Dependencies.getResourceManager().getResourceAsSprite("hdr_sprites/skybox/back.jpg")
    );

    public Skybox(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        this.graphicsManagerGL = (GraphicsManagerGL) ((PanelGL) getScene().getPanel()).getGraphicsManager3D();
        updateSkybox();
    }

    public void updateSkybox() {
        graphicsManagerGL.setSkyboxSprites(sprites);
    }

}
