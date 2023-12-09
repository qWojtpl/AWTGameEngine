package pl.AWTGameEngine.objects;

import java.awt.*;

public class Sprite {

    private final String imagePath;
    private final Image image;

    public Sprite(String imagePath, Image image) {
        this.imagePath = imagePath;
        this.image = image;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public Image getImage() {
        return this.image;
    }

}
