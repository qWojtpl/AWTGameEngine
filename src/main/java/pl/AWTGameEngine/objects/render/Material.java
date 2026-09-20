package pl.AWTGameEngine.objects.render;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.objects.ColorObject;

public class Material {

    private static Material defaultMaterial;

    private final String name;
    private Sprite sprite;
    private ColorObject color;
    private float opacity = 1;
    private int repeatTexture = 0;

    public Material(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public Material setSprite(Sprite sprite) {
        this.sprite = sprite;
        return this;
    }

    public float getOpacity() {
        return this.opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public int getRepeatTexture() {
        return this.repeatTexture;
    }

    public void setRepeatTexture(int repeatTexture) {
        this.repeatTexture = repeatTexture;
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
