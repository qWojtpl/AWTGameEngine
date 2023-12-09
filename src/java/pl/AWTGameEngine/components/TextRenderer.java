package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class TextRenderer extends ObjectComponent {

    private String text = "Text";
    private ColorObject color = new ColorObject();
    private float size = 30.0f;
    private HorizontalAlign horizontal = HorizontalAlign.LEFT;
    private VerticalAlign vertical = VerticalAlign.TOP;
    private TextWrap wrap = TextWrap.NO_WRAP;

    public TextRenderer(GameObject object) {
        super(object);
    }

    @Override
    public void onRender(Graphics g) {
        final String[] lines = text.split("\n");
        final int height = g.getFontMetrics(getWindow().getDefaultFont()).getHeight();
        final int totalHeight = lines.length * height;
        for(int i = 1; i <= lines.length; i++) {
            String line = lines[i - 1];
            int width = g.getFontMetrics(getWindow().getDefaultFont(size)).stringWidth(line);
            int x, y;
            if(TextWrap.WRAP.equals(wrap)) {
                //todo: implement wrap
            }
            if(HorizontalAlign.LEFT.equals(horizontal)) {
                x = 0;
            } else if (HorizontalAlign.RIGHT.equals(horizontal)) {
                x = getObject().getScaleX() - width;
            } else {
                x = (getObject().getScaleX() - width) / 2;
            }
            if(VerticalAlign.TOP.equals(vertical)) {
                y = height / 4;
            } else if (VerticalAlign.BOTTOM.equals(vertical)) {
                y = getObject().getScaleY() - totalHeight - height / 4;
            } else {
                y = (getObject().getScaleY() - totalHeight) / 2;
            }
            Graphics2D g2d = (Graphics2D) g;
            AffineTransform oldTransform = g2d.getTransform();
            if (getObject().getRotation() != 0) {
                AffineTransform transform = new AffineTransform();
                transform.rotate(Math.toRadians(getObject().getRotation()),
                        getCamera().parseX(getObject(), getObject().getCenterX()),
                        getCamera().parseY(getObject(), getObject().getCenterY()));
                g2d.transform(transform);
            }
            g2d.setColor(color.getColor());
            g2d.setFont(getWindow().getDefaultFont((getSize() * getCamera().getZoom())));
            g2d.drawString(line,
                    getCamera().parseX(getObject(), getObject().getX() + x),
                    getCamera().parseY(getObject(), getObject().getY() + y + height * i));
            g2d.setTransform(oldTransform);
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
    }

    public void setColor(ColorObject color) {
        if(color == null) {
            return;
        }
        this.color = color;
    }

    @SerializationSetter
    public void setColor(String color) {
        setColor(new ColorObject(color));
    }

    public void setSize(float size) {
        this.size = size;
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
