package pl.AWTGameEngine.custom.editor;

import pl.AWTGameEngine.components.*;
import pl.AWTGameEngine.objects.GameObject;

public class InfoComponent extends ObjectComponent {

    private ObjectComponent component;
    private GameObject componentLine;
    private GameObject iconObject;
    private GameObject textObject;

    public InfoComponent(GameObject object, ObjectComponent component) {
        super(object);
        this.component = component;
    }

    @Override
    public void onAddComponent() {
        BlankRenderer blankRenderer = new BlankRenderer(getObject());
        blankRenderer.setColor("rgb(40,40,40)");
        Border border = new Border(getObject());
        border.setColor("rgb(75,75,75)");
        getObject().addComponent(blankRenderer);
        getObject().addComponent(border);
        componentLine = getScene().createGameObject("@vo-" + getObject().getIdentifier() + "-line" + System.nanoTime());
        componentLine.setX(10);
        componentLine.setY(230);
        componentLine.setSize(290, 0);
        LineComponent lineComponent = new LineComponent(componentLine);
        lineComponent.setColor("rgb(75,75,75)");
        lineComponent.setThickness(1);
        componentLine.addComponent(lineComponent);
        componentLine.setParent(getObject());

        iconObject = getScene().createGameObject("@vo-" + getObject().getIdentifier() + "-icon" + System.nanoTime());
        iconObject.setX(10);
        iconObject.setY(210);
        iconObject.setSize(20, 20);
        SpriteRenderer spriteRenderer = new SpriteRenderer(iconObject);
        spriteRenderer.setSpriteSource("sprites/base/success.png");
        iconObject.addComponent(spriteRenderer);
        iconObject.setParent(getObject());

        textObject = getScene().createGameObject("@vo-" + getObject().getIdentifier() + "-text" + System.nanoTime());
        textObject.setX(35);
        textObject.setY(200);
        textObject.setSize(290, 10);
        TextRenderer textRenderer = new TextRenderer(textObject);
        textRenderer.setColor("WHITE");
        textRenderer.setSize(16);
        textRenderer.setText(component.getClass().getSimpleName());
        textObject.addComponent(textRenderer);
        textObject.setParent(getObject());
    }

    @Override
    public void onRemoveComponent() {
        getScene().removeGameObject(componentLine);
        getScene().removeGameObject(iconObject);
        getScene().removeGameObject(textObject);
    }

}
