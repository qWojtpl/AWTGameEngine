package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.BlankRenderer;
import pl.AWTGameEngine.components.Server;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.objects.ConnectedClient;
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
    public void onClientConnect(Server server, ConnectedClient client) {
        GameObject object = getScene().createGameObject("cube" + client.getId());
        object.setSize(new TransformSet(100, 100));
        object.setPosition(new TransformSet(300, 300));
        object.getNet().setOwner(client.getId());
        BlankRenderer blankRenderer = new BlankRenderer(object);
        blankRenderer.setColor("rgb(0,255,0)");
        object.addComponent(blankRenderer);
        Movement2D movement2D = new Movement2D(object);
        object.addComponent(movement2D);
        playerObjects.put(client.getId(), object);
        Logger.info("Created client object");
    }

    @Override
    public void onClientDisconnect(Server server, ConnectedClient client) {
        getScene().removeGameObject(playerObjects.get(client.getId()));
        playerObjects.remove(client.getId());
        Logger.info("Removed client object");
    }

}
