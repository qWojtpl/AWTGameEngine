package pl.AWTGameEngine.components;

import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.objects.GameObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        button = new JButton(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for(ObjectComponent component : getObject().getComponents()) {
                    component.onButtonClick();
                }
            }

        });
        getWindow().getPanel().add(button);
        button.setFocusable(false);
        if(image != null) {
            ImageIcon icon = new ImageIcon();
            icon.setImage(image);
            button.setIcon(icon);
        }
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setText(getText());
    }

    @Override
    public void onRemoveComponent() {
        getScene().getWindow().getPanel().remove(button);
    }

    @Override
    public void onRender(Graphics g) {
        if(button == null) {
            return;
        }
        button.setLocation((int) ((getObject().getX() - getObject().getScaleX() - getCamera().getRelativeX(getObject())) * getCamera().getZoom()),
                (int) ((getObject().getY() - getCamera().getRelativeY(getObject())) * getCamera().getZoom()));
        button.setSize((int) (getObject().getScaleX() * getCamera().getZoom()),
                (int) (getObject().getScaleY() * getCamera().getZoom()));
        if(image != null) {
            ImageIcon icon = new ImageIcon();
            icon.setImage(image);
            button.setIcon(icon);
        }
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
        if(image == null) {
            System.out.println("Image is null.");
            return;
        }
        this.image = image;
    }

    public void setImage(String imageSource) {
        setImage(ResourceManager.getResourceAsImage(imageSource));
    }

}
