package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

@Unique
public class Button extends ObjectComponent {

    private final BlankRenderer background = new BlankRenderer(getObject());
    private final TextRenderer textRenderer = new TextRenderer(getObject());
    private final Border border = new Border(getObject());
    private ColorObject backgroundColor = new ColorObject(Color.GRAY);
    private ColorObject highlightColor = new ColorObject(Color.BLACK);
    private ColorObject textColor = new ColorObject(Color.BLACK);
    private ColorObject highlightTextColor = new ColorObject(Color.GRAY);
    private final ColorObject backgroundComponentColor = new ColorObject();
    private final ColorObject textComponentColor = new ColorObject();
    private Action action = Action.DO_NOTHING;

    public Button(GameObject object) {
        super(object);
        background.setColor(backgroundComponentColor);
        textRenderer.setText("Button");
        textRenderer.setColor(textComponentColor);
        textRenderer.align(TextRenderer.HorizontalAlign.CENTER, TextRenderer.VerticalAlign.CENTER);
    }

    @Override
    public void onAddComponent() {
        getObject().addComponent(background);
        getObject().addComponent(border);
        getObject().addComponent(textRenderer);
    }

    @Override
    public void onRemoveComponent() {
        getObject().removeComponent(background);
        getObject().removeComponent(border);
        getObject().removeComponent(textRenderer);
    }

    @Override
    public void onUpdate() {
        onStaticUpdate();
    }

    @Override
    public void onStaticUpdate() {
        backgroundComponentColor.setColor(backgroundColor.getColor());
        textComponentColor.setColor(textColor.getColor());
        if(getMouseListener().getMouseX() >= getObject().getX()
        && getMouseListener().getMouseX() <= getObject().getX() + getObject().getScaleX()) {
            if(getMouseListener().getMouseY() >= getObject().getY()
            && getMouseListener().getMouseY() <= getObject().getY() + getObject().getScaleY()) {
                backgroundComponentColor.setColor(highlightColor.getColor());
                textComponentColor.setColor(highlightTextColor.getColor());
            }
        }
    }

    @Override
    public void onMouseClick() {
        for(ObjectComponent component : getObject().getComponents()) {
            component.onButtonClick();
        }
        if(Action.CLOSE_WINDOW.equals(action)) {
            getWindow().close();
        }
    }

    public TextRenderer getTextRenderer() {
        return this.textRenderer;
    }

    @SerializationGetter
    public String getText() {
        return textRenderer.getText();
    }

    @SerializationGetter
    public float getSize() {
        return textRenderer.getSize();
    }

    public ColorObject getBackgroundColor() {
        return this.backgroundColor;
    }

    public ColorObject getHighlightColor() {
        return this.highlightColor;
    }

    public ColorObject getTextColor() {
        return this.textColor;
    }

    public ColorObject getHighlightTextColor() {
        return this.highlightTextColor;
    }

    @SerializationGetter
    public Action getAction() {
        return this.action;
    }

    @SerializationSetter
    public void setText(String text) {
        textRenderer.setText(text);
    }

    public void setSize(float size) {
        textRenderer.setSize(size);
    }

    @SerializationSetter
    public void setSize(String size) {
        try {
            setSize(Float.parseFloat(size));
        } catch(NumberFormatException e) {
            setSize(20f);
        }
    }

    public void setBackgroundColor(ColorObject color) {
        this.backgroundColor = color;
    }

    @SerializationSetter
    public void setBackgroundColor(String color) {
        setBackgroundColor(new ColorObject(color));
    }

    public void setHighlightColor(ColorObject color) {
        this.highlightColor = color;
    }

    @SerializationSetter
    public void setHighlightColor(String color) {
        setHighlightColor(new ColorObject(color));
    }

    public void setTextColor(ColorObject color) {
        this.textColor = color;
    }

    @SerializationSetter
    public void setTextColor(String color) {
        setTextColor(new ColorObject(color));
    }

    public void setHighlightTextColor(ColorObject color) {
        this.highlightTextColor = color;
    }

    @SerializationSetter
    public void setHighlightTextColor(String color) {
        setHighlightTextColor(new ColorObject(color));
    }

    public void setAction(Action action) {
        if(action == null) {
            action = Action.DO_NOTHING;
        }
        this.action = action;
    }

    @SerializationSetter
    public void setAction(String action) {
        try {
            setAction(Action.valueOf(action.toUpperCase()));
        } catch(IllegalArgumentException e) {
            setAction(Action.DO_NOTHING);
        }
    }

    public enum Action {
        CLOSE_WINDOW,
        DO_NOTHING
    }

}
