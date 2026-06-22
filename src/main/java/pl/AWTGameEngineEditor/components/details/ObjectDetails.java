package pl.AWTGameEngineEditor.components.details;

import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.base.HTMLFileComponent;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.engine.panels.WebPanel;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngineEditor.manager.EditorManager;

@WebComponent
public class ObjectDetails extends HTMLFileComponent {

    private GameObject gameObject;
    private final StringBuilder panelBuilder = new StringBuilder();

    public ObjectDetails(GameObject object) {
        super(object);
    }

    @Override
    public void onUpdate() {
        checkDetailsFeedbacks();
    }

    @Override
    public String filterRender(String data) {
        if(gameObject == null) {
            return data;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("<option>- Object transform</option>");
        for(ObjectComponent component : gameObject.getComponents()) {
            builder.append("<option>");
            builder.append(component.getComponentName());
            builder.append("</option>");
        }
        return data.replace("{{components}}", builder).replace("{{panel}}", panelBuilder);
    }

    public void setup(String identifier) {
        this.gameObject = EditorManager.getInstance().getGameViewWindow().getCurrentScene().getGameObjectByName(identifier);
        createInput("identifier", "Identifier", "id", false, identifier);
        createSection("Position");
        createInput("position-x", "X", "pos.x", true, gameObject.getPosition().getX() + "");
        createInput("position-y", "Y", "pos.y", true, gameObject.getPosition().getY() + "");
        createInput("position-z", "Z", "pos.z", true, gameObject.getPosition().getZ() + "");
        createSection("Size");
        createInput("size-x", "X", "size.x", true, gameObject.getSize().getX() + "");
        createInput("size-y", "Y", "size.y", true, gameObject.getSize().getY() + "");
        createInput("size-z", "Z", "size.z", true, gameObject.getSize().getZ() + "");
        createSection("Rotation");
        createInput("rotation-x", "X", "rotation.x", true, gameObject.getRotation().getX() + "");
        createInput("rotation-y", "Y", "rotation.y", true, gameObject.getRotation().getY() + "");
        createInput("rotation-z", "Z", "rotation.z", true, gameObject.getRotation().getZ() + "");
        createCheckbox("rotation-quaternion", "Quaternion?", "rotation.quaternion");
        createInput("rotation-w", "W", "rotation.w", true, "0.0");
    }

    private void createInput(String name, String placeHolder, String feedBackIdentifier, boolean validateNumber, String defaultValue) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("<label for=\"%s\">", "objectdetails-" + name));
        builder.append(placeHolder);
        builder.append(String.format("</label><input type=\"text\" id=\"objectdetails-%s\" placeholder=\"%s\" ", name, placeHolder));
        if(validateNumber) {
            builder.append("oninput=\"validateInputNumber(this)\" ");
        }
        builder.append(
                String.format("onchange=\"feedback('%s', '%s', '%s:' + this.value);\" value=\"%s\"><br>",
                        this.getObject().getIdentifier(),
                        this.getClass().getCanonicalName(),
                        feedBackIdentifier,
                        defaultValue)
        );
        panelBuilder.append(builder);
    }

    private void createCheckbox(String name, String placeHolder, String feedBackIdentifier) {
        String builder = String.format("<label for=\"%s\">", "objectdetails-" + name) +
                placeHolder +
                String.format("</label><input type=\"checkbox\" id=\"objectdetails-%s\" onchange=\"feedback('%s', '%s', '%s:' + this.checked);\"><br>",
                        name,
                        this.getObject().getIdentifier(),
                        this.getClass().getCanonicalName(),
                        feedBackIdentifier);
        panelBuilder.append(builder);
    }

    private void createSection(String name) {
        String section = String.format("<h2>%s</h2><br>", name);
        panelBuilder.append(section);
    }

    private void checkDetailsFeedbacks() {
        WebGraphicsManager detailsGraphicsManager = ((WebPanel) getScene().getPanel()).getGraphicsManager();
        String feedback = detailsGraphicsManager.getFeedbacks(getObject().getIdentifier(), ObjectDetails.class);
        if(feedback == null) {
            return;
        }
        String[] split = feedback.split(":");
        switch(split[0]) {
            case "pos.x":
                gameObject.getPosition().setX(Double.parseDouble(split[1]));
                break;
            case "pos.y":
                gameObject.getPosition().setY(Double.parseDouble(split[1]));
                break;
            case "pos.z":
                gameObject.getPosition().setZ(Double.parseDouble(split[1]));
                break;
            case "size.x":
                gameObject.getSize().setX(Double.parseDouble(split[1]));
                break;
            case "size.y":
                gameObject.getSize().setY(Double.parseDouble(split[1]));
                break;
            case "size.z":
                gameObject.getSize().setZ(Double.parseDouble(split[1]));
                break;
            case "rotation.x":
                gameObject.setRotation(gameObject.getRotation().setX(Double.parseDouble(split[1])));
                break;
            case "rotation.y":
                gameObject.setRotation(gameObject.getRotation().setY(Double.parseDouble(split[1])));
                break;
            case "rotation.z":
                gameObject.setRotation(gameObject.getRotation().setZ(Double.parseDouble(split[1])));
                break;
            case "rotation.quaternion":
                if(split[1].equals("true")) {
                    setElement("rotation-x", gameObject.getQuaternionRotation().getX());
                    setElement("rotation-y", gameObject.getQuaternionRotation().getY());
                    setElement("rotation-z", gameObject.getQuaternionRotation().getZ());
                    setElement("rotation-w", gameObject.getQuaternionRotation().getW());
                } else {
                    setRotationElements(gameObject);
                }
                break;
        }
    }

    private void setElement(String name, Object value) {
        WebGraphicsManager detailsGraphicsManager = ((WebPanel) getScene().getPanel()).getGraphicsManager();
        detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-%s').value=\"%s\"", name, value));
    }

    private void setRotationElements(GameObject go) {
        setElement("rotation-x", go.getRotation().getX());
        setElement("rotation-y", go.getRotation().getY());
        setElement("rotation-z", go.getRotation().getZ());
    }

}
