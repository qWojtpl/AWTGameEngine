package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.GameObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class SpriteRenderer extends ObjectComponent {

    private Image image;

    @Override
    public void onRender(GameObject object, Graphics g) {
        g.drawImage(image, object.getX(), object.getY(), object.getScaleX(), object.getScaleY(), null);
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setImage(String imageSource) {
        try {
            URL url = this.getClass().getClassLoader().getResource(imageSource);
            if(url == null) {
                throw new IOException();
            }
            this.image = ImageIO.read(url);
        } catch(IOException e) {
            System.out.println("Can't set image to " + imageSource + "!");
        }
    }

}
