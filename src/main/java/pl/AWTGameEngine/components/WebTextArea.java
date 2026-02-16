package pl.AWTGameEngine.components;

import javafx.application.Platform;
import pl.AWTGameEngine.annotations.components.management.Conflicts;
import pl.AWTGameEngine.annotations.components.management.ConflictsWith;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.engine.panels.WebPanel;
import pl.AWTGameEngine.objects.GameObject;

@WebComponent
@Conflicts({
        @ConflictsWith(BlankRenderer.class),
        @ConflictsWith(TextRenderer.class)
})
public class WebTextArea extends ObjectComponent {

    private String text = "Text";
    private boolean propertyChanged = true;

    public WebTextArea(GameObject object) {
        super(object);
    }

    @Override
    public void onUpdate() {
        if(getKeyListener().getPressedKeys().isEmpty()) {
            return;
        }
        WebGraphicsManager manager = ((WebPanel) getObject().getScene().getPanel()).getGraphicsManager();
        Platform.runLater(() -> {
            text = (String) manager.getWebView().getEngine().executeScript(
                    String.format("getInputValue(\"%s\");", getObject().getIdentifier()));
        });
    }

    @Override
    public void onWebRenderRequest(WebGraphicsManager g) {
        if(!propertyChanged) {
            return;
        }
        g.execute(String.format("setInputValue(\"%s\", \"%s\");",
                getObject().getIdentifier(),
                getText()));
        propertyChanged = false;
    }

    public String getText() {
        return this.text;
    }

    @FromXML
    public void setText(String text) {
        this.text = text;
        propertyChanged = true;
    }

}
