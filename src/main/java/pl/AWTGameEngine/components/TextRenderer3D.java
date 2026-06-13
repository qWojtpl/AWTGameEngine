package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.components.base.Base3DShape;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.render.Sprite;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * TextRenderer3D sets the textures of all Base3DShapes which
 * are in the same object to rendered text.
 */
@ComponentGL
public class TextRenderer3D extends ObjectComponent {

    private String text;
    private String heldImage;
    private Sprite textSprite;

    public TextRenderer3D(GameObject object) {
        super(object);
    }

    private void updateText() {
        if(heldImage != null) {
            if(!this.text.equals(heldImage)) {
                textSprite = new Sprite(getFontImage());
                heldImage = new String(this.text);
            }
        } else {
            textSprite = new Sprite(getFontImage());
            heldImage = new String(this.text);
        }
        for(ObjectComponent component : getObject().getComponents()) {
            if(!(component instanceof Base3DShape)) {
                continue;
            }
            ((Base3DShape) component).setSprite(textSprite);
        }
    }

    private BufferedImage getFontImage() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setFont(getWindow().getFont(100));
        FontMetrics metrics = g.getFontMetrics();
        int width = metrics.stringWidth(text);
        int height = metrics.getHeight();
        int ascent = metrics.getAscent();
        if(width <= 0) {
            width = 1;
        }
        if(height <= 0) {
            height = 1;
        }
        g.dispose();
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(getWindow().getFont(100));
        g.drawString(text, 0, ascent);
        g.dispose();
        return image;
    }

    @Override
    public void onSerializationFinish() {
        updateText();
    }

    @SaveState(name = "text")
    public String getText() {
        return this.text;
    }

    @FromXML
    public void setText(String text) {
        this.text = text;
        updateText();
    }

}
