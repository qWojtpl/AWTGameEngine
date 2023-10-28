package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.awt.geom.AffineTransform;

@Unique
public class SpriteRenderer extends ObjectComponent {

    private Image image;

    public SpriteRenderer(GameObject object) {
        super(object);
    }

    @Override
    public void onRender(Graphics g) {
        if(image == null) {
            return;
        }
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldTransform = g2d.getTransform();
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(getObject().getRotation()),
                (getObject().getCenterX() - getCamera().getRelativeX(getObject())) * getCamera().getZoom(),
                (getObject().getCenterY() - getCamera().getRelativeY(getObject())) * getCamera().getZoom());
        g2d.transform(transform);
        g2d.drawImage(image, (int) ((getObject().getX() - getCamera().getRelativeX(getObject())) * getCamera().getZoom()),
                (int) ((getObject().getY() - getCamera().getRelativeY(getObject())) * getCamera().getZoom()),
                (int) (getObject().getScaleX() * getCamera().getZoom()), (int) (getObject().getScaleY() * getCamera().getZoom()),
                null);
        g2d.setTransform(oldTransform);
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setImage(String imageSource) {
        setImage(ResourceManager.getResourceAsImage(imageSource));
    }

}
