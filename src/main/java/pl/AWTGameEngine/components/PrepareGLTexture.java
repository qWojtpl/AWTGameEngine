package pl.AWTGameEngine.components;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.ComponentGL;
import pl.AWTGameEngine.annotations.FromXML;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.panels.PanelGL;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Sprite;

@ComponentGL
public class PrepareGLTexture extends ObjectComponent {

    private String name;
    private Sprite sprite;
    private boolean submit;

    public PrepareGLTexture(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        if(!(getPanel() instanceof PanelGL)) {
            return;
        }
        if(name == null) {
            Logger.error("Cannot prepare texture, because name is null.");
            return;
        }
        if(sprite == null) {
            Logger.error("Cannot prepare texture, because spriteSource is not set.");
            return;
        }
        ((PanelGL) getPanel()).prepareTexture(name, sprite);
        Logger.info("Prepared GL texture: " + name);
        if(submit) {
            ((PanelGL) getPanel()).submitInit();
        }
    }

    @FromXML
    public void setName(String name) {
        this.name = name;
    }

    @FromXML
    public void setSpriteSource(String spriteSource) {
        this.sprite = Dependencies.getResourceManager().getResourceAsSprite(spriteSource);
    }

    @FromXML
    public void setSubmit(String submit) {
        this.submit = Boolean.parseBoolean(submit);
    }

}
