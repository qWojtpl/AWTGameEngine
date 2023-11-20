package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.GameObject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class TextArea extends ObjectComponent {

    private String text = "Text";
    private final TextRenderer textRenderer = new TextRenderer(getObject());
    private final Border border = new Border(getObject());
    private boolean focused = false;
    private boolean cancelType = false;

    public TextArea(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        setText(text);
        textRenderer.align(TextRenderer.HorizontalAlign.LEFT, TextRenderer.VerticalAlign.TOP);
        getObject().addComponent(textRenderer);
        getObject().addComponent(border);
    }

    @Override
    public void onRemoveComponent() {
        getObject().removeComponent(textRenderer);
        getObject().addComponent(border);
    }

    @Override
    public void onKeyType(char key) {
        if(!isFocused()) {
            return;
        }
        if(cancelType) {
            cancelType = false;
            return;
        }
        if(getKeyListener().getPressedKeys().size() == 0) {
            return;
        }
        setText(text + key);
    }

    @Override
    public void onKeyType(int key) {
        // deleting text
    }

    @Override
    public void onMouseClick() {
        focused = true;
    }

    @Override
    public void onMouseClick(GameObject object) {
        if(!getObject().equals(object)) {
            focused = false;
        }
    }

    public String getText() {
        return this.text;
    }

    public TextRenderer getTextRenderer() {
        return this.textRenderer;
    }

    public boolean isFocused() {
        return this.focused;
    }

    public void setText(String text) {
        this.text = text;
        textRenderer.setText(text);
    }

}
