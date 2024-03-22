package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.engine.graphics.GraphicsManager;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.engine.graphics.WebRenderable;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.awt.font.FontRenderContext;

public class TextRenderer extends ObjectComponent implements WebRenderable {

    private String text = "Text";
    private ColorObject color = new ColorObject();
    private float size = 30.0f;
    private HorizontalAlign horizontal = HorizontalAlign.LEFT;
    private VerticalAlign vertical = VerticalAlign.TOP;
    private TextWrap wrap = TextWrap.NO_WRAP;
    private boolean propertyChanged = false;

    public TextRenderer(GameObject object) {
        super(object);
    }

    @Override
    public void onRender(GraphicsManager g) {
        final String[] lines = text.split("\n");
        final int height = g.getGraphics().getFontMetrics(getWindow().getFont(size)).getHeight();
        final int totalHeight = lines.length * height;
        for(int i = 1; i <= lines.length; i++) {
            String line = lines[i - 1];
            Font font = getWindow().getFont(size);
            int width = Math.round((float)
                    font.getStringBounds(line, new FontRenderContext(font.getTransform(), true, true))
                            .getBounds()
                            .getWidth()
            );
            int x, y;
            if(TextWrap.WRAP.equals(wrap)) {
                //todo: implement wrap
            }
            if(HorizontalAlign.LEFT.equals(horizontal)) {
                x = 0;
            } else if (HorizontalAlign.RIGHT.equals(horizontal)) {
                x = getObject().getSizeX() - width;
            } else {
                x = (getObject().getSizeX() - width) / 2;
            }
            if(VerticalAlign.TOP.equals(vertical)) {
                y = height / 4;
            } else if (VerticalAlign.BOTTOM.equals(vertical)) {
                y = getObject().getSizeY() - totalHeight - height / 4;
            } else {
                y = (getObject().getSizeY() - totalHeight) / 2 - height / 4;
            }
            g.drawString(
                    line,
                    getCamera().parseX(getObject(), getObject().getX() + x),
                    getCamera().parseY(getObject(), getObject().getY() + y + height * i),
                    new GraphicsManager.RenderOptions()
                            .setColor(color.getColor())
                            .setFont(getWindow().getFont(getSize() * getCamera().getZoom()))
                            .setRotation(getObject().getRotation())
                            .setRotationCenterX(getCamera().parseX(getObject(), getObject().getCenterX()))
                            .setRotationCenterY(getCamera().parseY(getObject(), getObject().getCenterY()))
            );
        }
    }

    @SerializationGetter
    public String getText() {
        return this.text;
    }

    public ColorObject getColorObject() {
        return this.color;
    }

    public String getColor() {
        return this.color.serialize();
    }

    @SerializationGetter
    public float getSize() {
        return this.size;
    }

    @SerializationGetter
    public HorizontalAlign getHorizontalAlign() {
        return this.horizontal;
    }

    @SerializationGetter
    public VerticalAlign getVerticalAlign() {
        return this.vertical;
    }

    @SerializationGetter
    public TextWrap getWrap() {
        return this.wrap;
    }

    @SerializationSetter
    public void setText(String text) {
        this.text = text;
        propertyChanged = true;
    }

    public void setColor(ColorObject color) {
        if(color == null) {
            return;
        }
        this.color = color;
        propertyChanged = true;
    }

    @SerializationSetter
    public void setColor(String color) {
        setColor(new ColorObject(color));
    }

    public void setSize(float size) {
        this.size = size;
        propertyChanged = true;
    }

    @SerializationSetter
    public void setSize(String size) {
        setSize(Float.parseFloat(size));
    }

    public void align(HorizontalAlign horizontal, VerticalAlign vertical) {
        align(horizontal);
        align(vertical);
    }

    public void align(HorizontalAlign horizontal) {
        this.horizontal = horizontal;
    }

    public void align(VerticalAlign vertical) {
        this.vertical = vertical;
    }

    @SerializationSetter
    public void setHorizontal(String horizontal) {
        HorizontalAlign align;
        try {
            align = HorizontalAlign.valueOf(horizontal.toUpperCase());
        } catch(IllegalArgumentException e) {
            align = HorizontalAlign.LEFT;
        }
        align(align);
    }

    @SerializationSetter
    public void setVertical(String vertical) {
        VerticalAlign align;
        try {
            align = VerticalAlign.valueOf(vertical.toUpperCase());
        } catch(IllegalArgumentException e) {
            align = VerticalAlign.TOP;
        }
        align(align);
    }

    public void setWrap(TextWrap wrap) {
        this.wrap = wrap;
    }

    @SerializationSetter
    public void setWrap(String wrap) {
        try {
            setWrap(TextWrap.valueOf(wrap.toUpperCase()));
        } catch(IllegalArgumentException e) {
            setWrap(TextWrap.NO_WRAP);
        }
    }

    @Override
    public void onWebRenderRequest(WebGraphicsManager g) {
        if(!propertyChanged) {
            return;
        }
        g.execute(String.format("setText(\"%s\", \"%s\", \"%s\", \"%s\")",
                getObject().getIdentifier(),
                text,
                color.serialize(),
                size));
        propertyChanged = false;
    }

    public enum HorizontalAlign {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum VerticalAlign {
        TOP,
        CENTER,
        BOTTOM
    }

    public enum TextWrap {
        WRAP,
        NO_WRAP,
        WRAP_AFTER_SPACE
    }

}
