package pl.AWTGameEngine.objects.render;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.objects.ColorObject;

public class Material {

    private static Material defaultMaterial;

    private final String name;
    private Sprite sprite;
    private ColorObject color;
    private float opacity = 1;
    private int repeatTexture = 0;
    private boolean transparentBlend = false;
    private boolean litMaterial = true;

    public Material(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    @FromXML
    public Material setSprite(Sprite sprite) {
        this.sprite = sprite;
        return this;
    }

    public float getOpacity() {
        return this.opacity;
    }

    @FromXML
    public Material setOpacity(float opacity) {
        this.opacity = opacity;
        return this;
    }

    public int getRepeatTexture() {
        return this.repeatTexture;
    }

    @FromXML
    public Material setRepeatTexture(int repeatTexture) {
        this.repeatTexture = repeatTexture;
        return this;
    }

    public boolean isTransparentBlend() {
        return this.transparentBlend;
    }

    /**
     * This should be set before material building in GraphicsManagerFilament. Setting it afterward
     * won't make a difference, since you need to rebuild the material.
     */
    @FromXML
    public Material setTransparentBlend(boolean blend) {
        this.transparentBlend = blend;
        return this;
    }

    public boolean isLitMaterial() {
        return this.litMaterial;
    }

    @FromXML
    public Material setLitMaterial(boolean litMaterial) {
        this.litMaterial = litMaterial;
        return this;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String serialize() {
        StringBuilder builder = new StringBuilder(name + ";");
        if(sprite != null) {
            builder.append(sprite.getImagePath());
        }
        builder.append(";");
        if(color != null) {
            builder.append(color.serialize());
        }
        builder.append(";");
        builder.append(opacity);
        builder.append(";");
        builder.append(repeatTexture);
        builder.append(";");
        return builder.toString();
    }

    public static Material getDefaultMaterial() {
        if(defaultMaterial == null) {
            defaultMaterial = new Material("DefaultMaterial")
                    .setSprite(Dependencies.getResourceManager().getResourceAsSprite("sprites/default.jpg"));
        }
        return defaultMaterial;
    }

}
