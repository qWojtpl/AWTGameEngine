package pl.AWTGameEngine.components;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.graphics.GraphicsManagerGL;
import pl.AWTGameEngine.engine.panels.PanelGL;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.render.Sprite;

import java.util.List;

@ComponentGL
public class Skybox extends ObjectComponent {

    private GraphicsManagerGL graphicsManagerGL;

    private final Sprite defaultSprite = Dependencies.getResourceManager().getResourceAsSprite("sprites/default.jpg");
    private Sprite rightSprite = defaultSprite;
    private Sprite leftSprite = defaultSprite;
    private Sprite topSprite = defaultSprite;
    private Sprite bottomSprite = defaultSprite;
    private Sprite frontSprite = defaultSprite;
    private Sprite backSprite = defaultSprite;

    public Skybox(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        this.graphicsManagerGL = (GraphicsManagerGL) ((PanelGL) getScene().getPanel()).getGraphicsManager3D();
        updateSkybox();
    }

    public void updateSkybox() {
        graphicsManagerGL.setSkyboxSprites(List.of(
                rightSprite,
                leftSprite,
                topSprite,
                bottomSprite,
                frontSprite,
                backSprite
        ));
    }

    @SaveState(name = "rightSprite")
    public Sprite getRightSprite() {
        return rightSprite;
    }

    @FromXML
    public void setRightSprite(Sprite sprite) {
        this.rightSprite = sprite;
    }

    @SaveState(name = "leftSprite")
    public Sprite getLeftSprite() {
        return leftSprite;
    }

    @FromXML
    public void setLeftSprite(Sprite sprite) {
        this.leftSprite = sprite;
    }

    @SaveState(name = "topSprite")
    public Sprite getTopSprite() {
        return topSprite;
    }

    @FromXML
    public void setTopSprite(Sprite sprite) {
        this.topSprite = sprite;
    }

    @SaveState(name = "bottomSprite")
    public Sprite getBottomSprite() {
        return bottomSprite;
    }

    @FromXML
    public void setBottomSprite(Sprite sprite) {
        this.bottomSprite = sprite;
    }

    @SaveState(name = "frontSprite")
    public Sprite getFrontSprite() {
        return frontSprite;
    }

    @FromXML
    public void setFrontSprite(Sprite sprite) {
        this.frontSprite = sprite;
    }

    @SaveState(name = "backSprite")
    public Sprite getBackSprite() {
        return backSprite;
    }

    @FromXML
    public void setBackSprite(Sprite sprite) {
        this.backSprite = sprite;
    }

}
