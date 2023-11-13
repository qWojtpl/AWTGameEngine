package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

@Unique
public class Button extends ObjectComponent {

    /*private String text = "Button";
    private JButton button;
    private Image image;

    public Button(GameObject object) {
        super(object);
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
    }*/

    private BlankRenderer background;
    private TextRenderer text;
    private ColorObject backgroundColor = new ColorObject();

    public Button(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        background = new BlankRenderer(getObject());
        background.setColor(backgroundColor);
        text = new TextRenderer(getObject());
        text.setText("button");
        text.setColor(new ColorObject(Color.RED));
        text.setSize(20);
        text.align(TextRenderer.Horizontal.CENTER, TextRenderer.Vertical.CENTER);
        getObject().addComponent(background);
        getObject().addComponent(text);
    }

    @Override
    public void onRemoveComponent() {
        getObject().removeComponent(background);
        getObject().removeComponent(text);
    }

    @Override
    public void onUpdate() {
        onStaticUpdate();
    }

    @Override
    public void onStaticUpdate() {
        backgroundColor.setColor(Color.BLACK);
        if(getMouseListener().getMouseX() >= getObject().getX() && getMouseListener().getMouseX() <= getObject().getX() + getObject().getScaleX()) {
            if(getMouseListener().getMouseY() >= getObject().getY() && getMouseListener().getMouseY() <= getObject().getY() + getObject().getScaleY()) {
                backgroundColor.setColor(Color.GREEN);
            }
        }
    }

    @Override
    public void onMouseClick(GameObject object) {
        if(!getObject().equals(object)) {
            return;
        }
        for(ObjectComponent component : getObject().getComponents()) {
            component.onButtonClick();
        }
        backgroundColor.transientAlpha(0, 1000);
    }

}
