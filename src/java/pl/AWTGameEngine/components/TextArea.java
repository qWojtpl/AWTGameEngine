package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.GameObject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class TextArea extends ObjectComponent {

    private String text = "Text";
    private final TextRenderer textRenderer = new TextRenderer(getObject());
    private final Border border = new Border(getObject());
    private boolean focused = false;
    private int pointerIterator = 0;
    private int pointerLocation = 0;

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
    public void onUpdate() {
        if(!isFocused()) {
            return;
        }
        textRenderer.setText(getRenderedText());
        if(pointerIterator >= getWindow().getLoop().getFPS()) {
            pointerIterator = 0;
        } else {
            pointerIterator++;
        }
    }

    @Override
    public void onKeyType(char key) {
        if(!isFocused()) {
            return;
        }
        String newText = "";
        for(int i = 0; i < text.length() + 1; i++) {
            if(i == pointerLocation) {
                newText += key;
            }
            if(i < text.length()) {
                newText += text.charAt(i);
            }
        }
        setText(newText);
        setPointerLocation(getPointerLocation() + 1);
    }

    @Override
    public void onKeyType(int key) {
        if(key == 37) {
            setPointerLocation(pointerLocation - 1);
        }
        if(key == 39) {
            setPointerLocation(pointerLocation + 1);
        }
    }

    @Override
    public void onMouseClick() {
        focused = true;
        setPointerLocation(text.length());
    }

    @Override
    public void onMouseClick(GameObject object) {
        if(!getObject().equals(object)) {
            focused = false;
        }
    }

    private String getRenderedText() {
        String rendered = "";
        for(int i = 0; i < text.length() + 1; i++) {
            if(pointerLocation == i && pointerIterator >= getWindow().getLoop().getFPS() / 2) {
                rendered += "|";
            }
            if(i < text.length()) {
                rendered += text.charAt(i);
            }
        }
        return rendered;
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

    public int getPointerLocation() {
        return this.pointerLocation;
    }

    public void setText(String text) {
        this.text = text;
        textRenderer.setText(text);
    }

    public void setPointerLocation(int location) {
        if(location < 0) {
            location = 0;
        }
        if(location > text.length()) {
            location = text.length();
        }
        this.pointerLocation = location;
    }

}
