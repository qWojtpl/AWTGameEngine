package pl.AWTGameEngine.components;

import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.objects.GameObject;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Button extends ObjectComponent {

    private String text = "Button";
    private JButton button;
    private Image image;

    public Button(GameObject object) {
        super(object);
        setUnique(true);
    }

    @Override
    public void onAddComponent() {
        button = new JButton(getText());
        getWindow().getPanel().add(button);
        button.setFocusable(false);
        ImageIcon icon = new ImageIcon();
        icon.setImage(image);
        button.setIcon(icon);
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
    }

    @Override
    public void onRemoveComponent() {
        getScene().getWindow().getPanel().remove(button);
    }

    @Override
    public void onRender(Graphics g) {
        button.setLocation((int) ((getObject().getX() - getObject().getScaleX() - getCamera().getRelativeX(getObject())) * getCamera().getZoom()),
                (int) ((getObject().getY() - getCamera().getRelativeY(getObject())) * getCamera().getZoom()));
        button.setSize((int) (getObject().getScaleX() * getCamera().getZoom()),
                (int) (getObject().getScaleY() * getCamera().getZoom()));
        ImageIcon icon = new ImageIcon();
        icon.setImage(image);
        button.setIcon(icon);
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
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
