package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.ConflictsWith;
import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

@Unique
public class Button extends ObjectComponent {

    private BlankRenderer background;
    private TextRenderer textRenderer;
    private ColorObject backgroundColor = new ColorObject(Color.GRAY);
    private ColorObject highlightColor = new ColorObject(Color.BLACK);
    private ColorObject textColor = new ColorObject(Color.BLACK);
    private ColorObject highlightTextColor = new ColorObject(Color.GRAY);
    private final ColorObject backgroundComponentColor = new ColorObject();
    private final ColorObject textComponentColor = new ColorObject();

    public Button(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        background = new BlankRenderer(getObject());
        background.setColor(backgroundComponentColor);
        textRenderer = new TextRenderer(getObject());
        textRenderer.setText("Button");
        textRenderer.setColor(textComponentColor);
        textRenderer.setSize(20);
        textRenderer.align(TextRenderer.Horizontal.CENTER, TextRenderer.Vertical.CENTER);
        getObject().addComponent(background);
        getObject().addComponent(textRenderer);
    }

    @Override
    public void onRemoveComponent() {
        getObject().removeComponent(background);
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
        if(getMouseListener().getMouseX() >= getObject().getX() && getMouseListener().getMouseX() <= getObject().getX() + getObject().getScaleX()) {
            if(getMouseListener().getMouseY() >= getObject().getY() && getMouseListener().getMouseY() <= getObject().getY() + getObject().getScaleY()) {
                backgroundComponentColor.setColor(highlightColor.getColor());
                textComponentColor.setColor(highlightTextColor.getColor());
            }
        }
    }

    @Override
    public void onMouseClick(GameObject object) {
        if(!getObject().equals(object)) {
            return;
        }
        for(ObjectComponent component : getObject().getComponents()) {
            component.onButtonClick();
        }
        //backgroundColor.transientAlpha(0, 1000);
    }

    public TextRenderer getTextRenderer() {
        return this.textRenderer;
    }

    public String getText() {
        return textRenderer.getText();
    }

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

    public void setText(String text) {
        textRenderer.setText(text);
    }

    public void setSize(float size) {
        textRenderer.setSize(size);
    }

    public void setBackgroundColor(ColorObject color) {
        this.backgroundColor = color;
    }

    public void setBackgroundColor(String color) {
        this.backgroundColor = new ColorObject(color);
    }

    public void setHighlightColor(ColorObject color) {
        this.highlightColor = color;
    }

    public void setHighlightColor(String color) {
        this.highlightColor = new ColorObject(color);
    }

    public void setTextColor(ColorObject color) {
        this.textColor = color;
    }

    public void setTextColor(String color) {
        this.textColor = new ColorObject(color);
    }

    public void setHighlightTextColor(ColorObject color) {
        this.highlightTextColor = color;
    }

    public void setHighlightTextColor(String color) {
        this.highlightTextColor = new ColorObject(color);
    }


}
