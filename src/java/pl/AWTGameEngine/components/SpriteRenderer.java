package pl.AWTGameEngine.components;

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
        g.drawImage(image, (int) ((getObject().getX() - getCamera().getRelativeX(getObject())) * getCamera().getZoom()),
                (int) ((getObject().getY() - getCamera().getRelativeY(getObject())) * getCamera().getZoom()),
                (int) (getObject().getScaleX() * getCamera().getZoom()), (int) (getObject().getScaleY() * getCamera().getZoom()),
                null);
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
