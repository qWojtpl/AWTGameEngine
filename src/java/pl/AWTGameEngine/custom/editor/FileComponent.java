package pl.AWTGameEngine.custom.editor;

import pl.AWTGameEngine.components.BlankRenderer;
import pl.AWTGameEngine.components.ObjectComponent;
import pl.AWTGameEngine.components.SpriteRenderer;
import pl.AWTGameEngine.components.TextRenderer;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Sprite;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FileComponent extends ObjectComponent {

    private final File file;
    private final Editor editor;
    private final BlankRenderer selectRenderer = new BlankRenderer(getObject());
    private final TextRenderer textRenderer = new TextRenderer(getObject());
    private final SpriteRenderer spriteRenderer = new SpriteRenderer(getObject());
    private final ColorObject highlightColor = new ColorObject("rgba(50,50,50,250)");
    private final ColorObject normalColor = new ColorObject("rgba(50,50,50,0)");
    private long lastClick = System.nanoTime();

    public FileComponent(GameObject object, File file, Editor editor) {
        super(object);
        this.file = file;
        this.editor = editor;
    }

    @Override
    public void onAddComponent() {
        selectRenderer.setColor(normalColor);
        textRenderer.setColor(new ColorObject(Color.WHITE));
        textRenderer.setSize(12);
        textRenderer.align(TextRenderer.HorizontalAlign.CENTER, TextRenderer.VerticalAlign.BOTTOM);
        getObject().addComponent(selectRenderer);
        getObject().addComponent(textRenderer);
        getObject().addComponent(spriteRenderer);
    }

    @Override
    public void onRemoveComponent() {
        getObject().removeComponent(selectRenderer);
        getObject().removeComponent(textRenderer);
        getObject().removeComponent(spriteRenderer);
    }

    @Override
    public void onMouseClick() {
        if(highlightColor.equals(selectRenderer.getColorObject())) {
            selectRenderer.setColor(normalColor);
            if(System.nanoTime() - lastClick < Math.pow(10, 9) * 0.25) {
                editor.openFile(this);
            }
        } else {
            selectRenderer.setColor(highlightColor);
            lastClick = System.nanoTime();
        }
    }

    @Override
    public void onMouseClick(GameObject object) {
        if(!getObject().equals(object)) {
            selectRenderer.setColor(normalColor);
        }
    }

    public String getText() {
        return this.textRenderer.getText();
    }

    public Sprite getSprite() {
        return this.spriteRenderer.getSprite();
    }

    public File getFile() {
        return this.file;
    }

    public void setText(String text) {
        textRenderer.setText(text);
    }

    public void setSprite(Sprite sprite) {
        spriteRenderer.setSprite(sprite);
    }

}
