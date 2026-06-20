package pl.AWTGameEngineEditor.components.details;

import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.base.HTMLFileComponent;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.engine.panels.WebPanel;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngineEditor.components.ObjectsHierarchy;
import pl.AWTGameEngineEditor.manager.EditorManager;

@WebComponent
public class ObjectDetails extends HTMLFileComponent {

    private String identifier;

    public ObjectDetails(GameObject object) {
        super(object);
    }

    @Override
    public void onUpdate() {
        checkDetailsFeedbacks();
    }

    public void setup(String identifier) {
        this.identifier = identifier;
        GameObject go = EditorManager.getInstance().getGameViewWindow().getCurrentScene().getGameObjectByName(identifier);
        WebGraphicsManager detailsGraphicsManager = ((WebPanel) getScene().getPanel()).getGraphicsManager();
        detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-identifier').value=\"%s\"", identifier));
        detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-position-x').value=%s", go.getPosition().getX()));
        detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-position-y').value=%s", go.getPosition().getY()));
        detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-position-z').value=%s", go.getPosition().getZ()));
        detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-size-x').value=%s", go.getSize().getX()));
        detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-size-y').value=%s", go.getSize().getY()));
        detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-size-z').value=%s", go.getSize().getZ()));
        detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-rotation-x').value=%s", go.getRotation().getZ()));
        detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-rotation-y').value=%s", go.getRotation().getZ()));
        detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-rotation-z').value=%s", go.getRotation().getZ()));
    }

    private void checkDetailsFeedbacks() {
        WebGraphicsManager detailsGraphicsManager = ((WebPanel) getScene().getPanel()).getGraphicsManager();
        String feedback = detailsGraphicsManager.getFeedbacks(getObject().getIdentifier(), ObjectDetails.class);
        if(feedback == null) {
            return;
        }
        GameObject go = EditorManager.getInstance().getGameViewWindow().getCurrentScene().getGameObjectByName(identifier);
        String[] split = feedback.split(":");
        switch(split[0]) {
            case "pos.x":
                go.getPosition().setX(Double.parseDouble(split[1]));
                break;
            case "pos.y":
                go.getPosition().setY(Double.parseDouble(split[1]));
                break;
            case "pos.z":
                go.getPosition().setZ(Double.parseDouble(split[1]));
                break;
            case "size.x":
                go.getSize().setX(Double.parseDouble(split[1]));
                break;
            case "size.y":
                go.getSize().setY(Double.parseDouble(split[1]));
                break;
            case "size.z":
                go.getSize().setZ(Double.parseDouble(split[1]));
                break;
            case "rotation.x":
                go.setRotation(go.getRotation().setX(Double.parseDouble(split[1])));
                break;
            case "rotation.y":
                go.setRotation(go.getRotation().setY(Double.parseDouble(split[1])));
                break;
            case "rotation.z":
                go.setRotation(go.getRotation().setZ(Double.parseDouble(split[1])));
                break;
            case "rotation.quaternion":
                if(split[1].equals("true")) {
                    detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-rotation-x').value=%s", go.getQuaternionRotation().getX()));
                    detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-rotation-y').value=%s", go.getQuaternionRotation().getY()));
                    detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-rotation-z').value=%s", go.getQuaternionRotation().getZ()));
                    detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-rotation-w').value=%s", go.getQuaternionRotation().getW()));
                } else {
                    detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-rotation-x').value=%s", go.getRotation().getX()));
                    detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-rotation-y').value=%s", go.getRotation().getY()));
                    detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-rotation-z').value=%s", go.getRotation().getZ()));
                }
                break;
        }
    }
}
