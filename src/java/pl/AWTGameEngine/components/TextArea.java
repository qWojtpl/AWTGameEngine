package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

public class TextArea extends ObjectComponent {

    private String text = "Text";
    private final BlankRenderer background = new BlankRenderer(getObject());
    private final BlankRenderer backgroundDisabled = new BlankRenderer(getObject());
    private final TextRenderer textRenderer = new TextRenderer(getObject());
    private final Border border = new Border(getObject());
    private boolean focused = false;
    private boolean disabled = false;
    private int pointerIterator = 0;
    private int pointerLocation = 0;
    private int selectionStart = -1;
    private int selectionEnd = -1;

    public TextArea(GameObject object) {
        super(object);
        setText(text);
        textRenderer.align(TextRenderer.HorizontalAlign.LEFT, TextRenderer.VerticalAlign.TOP);
        textRenderer.setWrap(TextRenderer.TextWrap.WRAP);
        background.setColor(new ColorObject(Color.WHITE));
        backgroundDisabled.setColor(new ColorObject(ColorObject.deserialize("rgba(222,222,222,0)")));
    }

    @Override
    public void onAddComponent() {
        getObject().addComponent(background);
        getObject().addComponent(backgroundDisabled);
        getObject().addComponent(textRenderer);
        getObject().addComponent(border);
    }

    @Override
    public void onRemoveComponent() {
        getObject().removeComponent(background);
        getObject().removeComponent(backgroundDisabled);
        getObject().removeComponent(textRenderer);
        getObject().removeComponent(border);
    }

    @Override
    public void onUpdate() {
        textRenderer.setText(getRenderedText());
        if(pointerIterator >= getWindow().getLoop().getFPS()) {
            pointerIterator = 0;
        } else {
            pointerIterator++;
        }
    }

    @Override
    public void onKeyType(char key) {
        if(!isFocused() || isDisabled()) {
            return;
        }
        write(key);
    }

    @Override
    public void onKeyType(int keyCode) {
        if(!isFocused() || isDisabled()) {
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
        } else if(key == 1) {
            selectionStart = 0;
            selectionEnd = text.length();
            return;
        } else if(key == 3) {
            copyText(getSelectedText());
            return;
        } else if(key == 22) {
            String clipboardText = getClipboardText();
            for(int i = 0; i < clipboardText.length(); i++) {
                write(clipboardText.charAt(i));
            }
            resetSelection();
            return;
        } else if(key == 24) {
            copyText(getSelectedText());
            deleteSelectedText();
            return;
        }
        String newText = "";
        for(int i = 0; i <= text.length(); i++) {
            if(i == pointerLocation) {
                newText += key;
            }
            if(i >= selectionStart && i <= selectionEnd) {
                continue;
            }
            if(i < text.length()) {
                newText += text.charAt(i);
            }
        }
        setText(newText);
        setPointerLocation(getPointerLocation() + 1);
        resetSelection();
    }

    public void deleteBack() {
        if(!isFocused()) {
            return;
        }
        if(selectionStart != -1 && selectionEnd != -1) {
            deleteSelectedText();
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
        resetSelection();
    }

    public void deleteNext() {
        if(selectionStart != -1 && selectionEnd != -1) {
            deleteSelectedText();
            return;
        }
        if(getPointerLocation() == text.length()) {
            return;
        }
        setPointerLocation(getPointerLocation() + 1);
        deleteBack();
    }

    public void resetSelection() {
        selectionStart = -1;
        selectionEnd = -1;
    }

    public void copyText(String text) {
        StringSelection textToCopy = new StringSelection(text);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(textToCopy, textToCopy);
    }

    public void deleteSelectedText() {
        String newText = "";
        for(int i = 0; i < text.length(); i++) {
            if(i >= selectionStart && i <= selectionEnd) {
                continue;
            }
            newText += text.charAt(i);
        }
        setText(newText);
        resetSelection();
        setPointerLocation(text.length());
    }

    public String getClipboardText() {
        String clipboardText;
        try {
            clipboardText = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch(Exception e) {
            return "";
        }
        return clipboardText;
    }

    public String getSelectedText() {
        String selectedText = "";
        for(int i = selectionStart; i < selectionEnd; i++) {
            selectedText += text.charAt(i);
        }
        return selectedText;
    }

    private String getRenderedText() {
        if(!isFocused() || isDisabled()) {
            return text;
        }
        String rendered = "";
        for(int i = 0; i <= text.length(); i++) {
            if(pointerLocation == i && pointerIterator >= getWindow().getLoop().getFPS() / 2) {
                rendered += "|";
            }
            if(i < text.length()) {
                rendered += text.charAt(i);
            } else if(i != 0) {
                if(text.charAt(i - 1) == '\n' && rendered.charAt(rendered.length() - 1) != '|') {
                    rendered += " ";
                }
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

    public boolean isDisabled() {
        return this.disabled;
    }

    public int getPointerLocation() {
        return this.pointerLocation;
    }

    @SerializationSetter
    public void setText(String text) {
        this.text = text;
        textRenderer.setText(text);
    }

    public void setDisabled(boolean disabled) {
        if(disabled) {
            backgroundDisabled.getColor().transientAlpha(255, 100);
        } else {
            backgroundDisabled.getColor().transientAlpha(0, 100);
        }
        this.disabled = disabled;
    }

    @SerializationSetter
    public void setDisabled(String disabled) {
        setDisabled(Boolean.parseBoolean(disabled));
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

    public void setBackgroundColor(ColorObject color) {
        background.setColor(color);
    }

    @SerializationSetter
    public void setBackgroundColor(String color) {
        setBackgroundColor(new ColorObject(color));
    }

    public void setDisabledColor(ColorObject color) {
        backgroundDisabled.setColor(color);
    }

    @SerializationSetter
    public void setDisabledColor(String color) {
        setDisabledColor(new ColorObject(color));
    }

    public void setTextColor(ColorObject color) {
        textRenderer.setColor(color);
    }

    @SerializationSetter
    public void setTextColor(String color) {
        setDisabledColor(new ColorObject(color));
    }

    public void setBorderColor(ColorObject color) {
        border.setColor(color);
    }

    @SerializationSetter
    public void setBorderColor(String color) {
        setBorderColor(new ColorObject(color));
    }

}
