package pl.AWTGameEngine.components;

import pl.AWTGameEngine.Main;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

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

}
