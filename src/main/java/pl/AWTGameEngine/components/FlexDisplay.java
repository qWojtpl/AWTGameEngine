package pl.AWTGameEngine.components;

import javafx.application.Platform;
import pl.AWTGameEngine.annotations.FromXML;
import pl.AWTGameEngine.annotations.WebComponent;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.RenderEngine;
import pl.AWTGameEngine.engine.panels.WebPanel;
import pl.AWTGameEngine.objects.GameObject;

import java.util.ArrayList;
import java.util.List;

@WebComponent
public class FlexDisplay extends ObjectComponent {

    private final List<GameObject> items = new ArrayList<>();

    public FlexDisplay(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        if(isWebRenderEngine()) {
            Platform.runLater(() -> {
                ((WebPanel) getScene().getPanel()).getGraphicsManager().execute(
                        String.format("setFlex(\"%s\");", getObject().getIdentifier()));
                ((WebPanel) getScene().getPanel()).getGraphicsManager().execute(
                        String.format("setFlexAlignItems(\"%s\", \"%s\");", getObject().getIdentifier(), "center"));
                ((WebPanel) getScene().getPanel()).getGraphicsManager().execute(
                        String.format("setFlexJustifyContent(\"%s\", \"%s\");", getObject().getIdentifier(), "center"));
                ((WebPanel) getScene().getPanel()).getGraphicsManager().execute(
                        String.format("setFlexGap(\"%s\", \"%s\");", getObject().getIdentifier(), "50"));
                for(GameObject object : items) {
                    ((WebPanel) getScene().getPanel()).getGraphicsManager().execute(
                            String.format("appendFlexChild(\"%s\", \"%s\");",
                                    getObject().getIdentifier(), object.getIdentifier()));
                }
            });
        } else {
            updatePositions();
        }
    }

    @Override
    public void onRemoveComponent() {
        if(isWebRenderEngine()) {
            //todo
        } else {
            updatePositions();
        }
    }

    @Override
    public boolean onUpdateSize(double newX, double newY) {
        updatePositions();
        return true;
    }

    @Override
    public boolean onUpdateSize(double newX, double newY, double newZ) {
        updatePositions();
        return true;
    }

    private void updatePositions() {
        if(isWebRenderEngine()) {
            return;
        }
        //todo
    }

    private boolean isWebRenderEngine() {
        return RenderEngine.WEB.equals(getScene().getRenderEngine());
    }

    public List<GameObject> getItems() {
        return new ArrayList<>(items);
    }

    @FromXML
    public void setItems(String itemsString) {
        String[] itemArray = itemsString.split(",");
        for(String item : itemArray) {
            items.add(getScene().getGameObjectByName(item));
        }
    }

}
