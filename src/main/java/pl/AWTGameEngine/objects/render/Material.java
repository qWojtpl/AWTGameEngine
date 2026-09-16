package pl.AWTGameEngine.objects.render;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.objects.ColorObject;

public class Material {

    private static Material defaultMaterial;

    private final String name;
    private Sprite sprite;
    private ColorObject color;
    private float opacity = 1;
    private float repeatTexture = 0;

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

    @Override
    public String toString() {
        return this.name;
    }

    public static Material getDefaultMaterial() {
        if(defaultMaterial == null) {
            defaultMaterial = new Material("DefaultMaterial")
                    .setSprite(Dependencies.getResourceManager().getResourceAsSprite("sprites/default.jpg"));
        }
        return defaultMaterial;
    }

}
