package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class SpriteRenderer extends ObjectComponent {

    private Image image;

    public SpriteRenderer(GameObject object) {
        super(object);
        setUnique(true);
    }

    @Override
    public void onRender(Graphics g) {
        g.drawImage(image, (int) ((object.getX() - Camera.getRelativeX(object)) * Camera.getZoom()),
                (int) ((object.getY() - Camera.getRelativeY(object)) * Camera.getZoom()),
                (int) (object.getScaleX() * Camera.getZoom()), (int) (object.getScaleY() * Camera.getZoom()), null);
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Image setImage(String imageSource) {
        try {
            URL url = this.getClass().getClassLoader().getResource(imageSource);
            if(url == null) {
                throw new IOException();
            }
            this.image = ImageIO.read(url);
        } catch(IOException e) {
            System.out.println("Can't set image to " + imageSource + "!");
        }
        return this.image;
    }

}
