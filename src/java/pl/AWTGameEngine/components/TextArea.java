package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.GameObject;

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
        textRenderer.align(TextRenderer.HorizontalAlign.CENTER, TextRenderer.VerticalAlign.CENTER);
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
        write(key);
    }

    @Override
    public void onKeyType(int keyCode) {
        if(!isFocused()) {
            return;
        }
        if(keyCode == 37) {
            setPointerLocation(getPointerLocation() - 1);
        } else if(keyCode == 39) {
            setPointerLocation(getPointerLocation() + 1);
        } else if(keyCode == 8) {
            deleteBack();
        } else if(keyCode == 127) {
            deleteNext();
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

    public void write(char key) {
        if(key == 8 || key == 127) {
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

    public void deleteBack() {
        if(!isFocused()) {
            return;
        }
        String newText = "";
        for(int i = 0; i < text.length(); i++) {
            if(i == pointerLocation - 1) {
                continue;
            }
            newText += text.charAt(i);
        }
        setText(newText);
        setPointerLocation(getPointerLocation() - 1);
    }

    public void deleteNext() {
        if(getPointerLocation() == text.length()) {
            return;
        }
        setPointerLocation(getPointerLocation() + 1);
        deleteBack();
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
