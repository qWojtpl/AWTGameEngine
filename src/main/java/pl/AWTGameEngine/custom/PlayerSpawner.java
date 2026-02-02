package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.BlankRenderer;
import pl.AWTGameEngine.components.Server;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.TransformSet;

import java.util.HashMap;

@DefaultComponent
@WebComponent
public class PlayerSpawner extends ObjectComponent {

    private final HashMap<Integer, GameObject> playerObjects = new HashMap<>();

    public PlayerSpawner(GameObject object) {
        super(object);
    }

    @Override
    public void onClientConnect(Server server, int clientId) {
        GameObject object = getScene().createGameObject("cube" + clientId);
        object.setSize(new TransformSet(100, 100));
        object.setPosition(new TransformSet(300, 300));
        object.getNet().setOwner(clientId);
        BlankRenderer blankRenderer = new BlankRenderer(object);
        blankRenderer.setColor("rgb(0,255,0)");
        object.addComponent(blankRenderer);
        Movement2D movement2D = new Movement2D(object);
        object.addComponent(movement2D);
        playerObjects.put(clientId, object);
        Logger.info("Created client object");
    }

    @Override
    public void onClientDisconnect(Server server, int clientId) {
        getScene().removeGameObject(playerObjects.get(clientId));
        playerObjects.remove(clientId);
        Logger.info("Removed client object");
    }

}
