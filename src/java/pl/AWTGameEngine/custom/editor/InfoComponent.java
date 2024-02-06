package pl.AWTGameEngine.custom.editor;

import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.components.*;
import pl.AWTGameEngine.engine.BindingsManager;
import pl.AWTGameEngine.objects.BindableProperty;
import pl.AWTGameEngine.objects.GameObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class InfoComponent extends ObjectComponent {

    private final ObjectComponent component;
    private GameObject componentLine;
    private GameObject iconObject;
    private GameObject textObject;
    private GameObject flexObject;
    private List<GameObject> bindObjects = new ArrayList<>();

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
        createLine();
        createIcon();
        createText();
        createFlex();
        createSerializationFields();
    }

    @Override
    public void onRemoveComponent() {
        getScene().removeGameObject(componentLine);
        getScene().removeGameObject(iconObject);
        getScene().removeGameObject(textObject);
        getScene().removeGameObject(flexObject);
        for(GameObject bindObject : bindObjects) {
            getScene().removeGameObject(bindObject);
        }
    }

    private void createLine() {
        componentLine = getScene().createGameObject("@vo-" + getObject().getIdentifier() + "-line" + System.nanoTime());
        componentLine.setX(10);
        componentLine.setY(230);
        componentLine.setSize(290, 0);
        LineComponent lineComponent = new LineComponent(componentLine);
        lineComponent.setColor("rgb(75,75,75)");
        lineComponent.setThickness(1);
        componentLine.addComponent(lineComponent);
        componentLine.setParent(getObject());
    }

    private void createIcon() {
        iconObject = getScene().createGameObject("@vo-" + getObject().getIdentifier() + "-icon" + System.nanoTime());
        iconObject.setX(10);
        iconObject.setY(210);
        iconObject.setSize(20, 20);
        SpriteRenderer spriteRenderer = new SpriteRenderer(iconObject);
        spriteRenderer.setSpriteSource("sprites/base/success.png");
        iconObject.addComponent(spriteRenderer);
        iconObject.setParent(getObject());
    }

    private void createText() {
        textObject = getScene().createGameObject("@vo-" + getObject().getIdentifier() + "-text" + System.nanoTime());
        textObject.setX(35);
        textObject.setY(200);
        textObject.setSize(290, 100);
        TextRenderer textRenderer = new TextRenderer(textObject);
        textRenderer.setColor("WHITE");
        textRenderer.setSize(16);
        textRenderer.setText(component.getClass().getSimpleName());
        textObject.addComponent(textRenderer);
        textObject.setParent(getObject());
    }

    private void createFlex() {
        flexObject = getScene().createGameObject("@vo-" + getObject().getIdentifier() + "-flex" + System.nanoTime());
        flexObject.setX(10);
        flexObject.setY(231);
        flexObject.setSize(290, 279);
        FlexComponent flexComponent = new FlexComponent(flexObject);
        flexComponent.setMinimumHeight(75);
        flexObject.addComponent(flexComponent);
        BindableProperty bp = new BindableProperty(this, flexComponent, "calculatedHeight", getObject(), "sizeY");
        
        flexObject.setParent(getObject());
    }

    private void createSerializationFields() {
        List<String> fieldNames = new ArrayList<>();
        for(Method method : component.getClass().getDeclaredMethods()) {
            if(!method.isAnnotationPresent(SerializationSetter.class)) {
                continue;
            }
            fieldNames.add(method.getName().substring(3));
        }
        for(String fieldName : fieldNames) {
            GameObject label = getScene().createGameObject("@vo-" + getObject().getIdentifier() + "-field" + System.nanoTime());
            TextRenderer textRenderer = new TextRenderer(label);
            textRenderer.setText(fieldName);
            textRenderer.align(TextRenderer.HorizontalAlign.LEFT, TextRenderer.VerticalAlign.CENTER);
            textRenderer.setSize(16);
            textRenderer.setColor("WHITE");
            label.addComponent(textRenderer);
            label.setSize(150, 32);
            label.setParent(flexObject);
            System.out.println(label.getSerializeString());
            bindObjects.add(label);
            //bindObjects.add(textArea);
        }
    }

}
