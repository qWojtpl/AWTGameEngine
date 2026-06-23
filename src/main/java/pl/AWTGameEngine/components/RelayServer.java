package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.components.base.NetComponent;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.deserializers.NetDeserializer;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.net.ConnectedClient;
import pl.AWTGameEngine.objects.net.NetBlock;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@DefaultComponent
@WebComponent
@ComponentGL
public class RelayServer extends NetComponent {

    private String relayServerAddress;
    private ConnectedClient relayConnection;
    private int sessionId;

    public RelayServer(GameObject object) {
        super(object);
    }

    @Override
    public void onSerializationFinish() {
        if(relayServerAddress == null) {
            Logger.warning("Component won't be initialized - relayServerAddress is not set.");
            return;
        }
        Logger.info("Connecting to relay server (" + relayServerAddress + ")...");
        try {
            String[] address = relayServerAddress.split(":");
            relayConnection = new ConnectedClient(0, new Socket(address[0], Integer.parseInt(address[1])));
        } catch(Exception e) {
            Logger.exception("Cannot connect to relay server", e);
            return;
        }
        Logger.info("Sending request to " + relayServerAddress + " to initialize a session...");
        relayConnection.sendMessage("$initSession");
        Logger.info("Waiting for response...");
        String response;
        try {
            response = relayConnection.getBufferedReader().readLine();
        } catch(Exception e) {
            Logger.exception("Cannot read response", e);
            return;
        }
        if(!response.startsWith("$sessionId:")) {
            Logger.error("Wrong relay response: " + response);
            return;
        }
        sessionId = Integer.parseInt(response.replaceFirst("\\$sessionId:", ""));
        Logger.info("Received sessionId: " + sessionId);
        new Thread(() -> {
            try {
                String data = relayConnection.getBufferedReader().readLine();
                if(!data.startsWith("$")) {
                    NetDeserializer.deserialize(getScene(), data, null);
                }
            } catch(Exception e) {
                Logger.exception("Exception while reading data from relay server", e);
                // disconnect
            }
        }).start();
    }

    @Override
    public void onNetUpdate() {
        sendObjectsPosition();
        sendComponents();
    }

    private void sendObjectsPosition() {
        List<NetBlock> blocks = new ArrayList<>();
        for(GameObject object : getScene().getGameObjects()) {
            NetBlock block = object.getNet().onPositionSynchronize();
            if(block.isEmpty()) {
                continue;
            }
            if(block.getIdentifier() != null) {
                blocks.add(block);
            } else {
                Logger.error("Incorrect NetBlock in " + object.getIdentifier());
            }
        }
        for(NetBlock block : blocks) {
            relayConnection.sendBlock(block);
        }
    }

    private void sendComponents() {
        List<NetBlock> blocks = new ArrayList<>();
        for(ObjectComponent component : getScene().getSceneEventHandler().getComponents("onSynchronize")) {
            NetComponent netComponent = (NetComponent) component;
            if(!netComponent.canSynchronize()) {
                continue;
            }
            NetBlock block = netComponent.onSynchronize();
            if(block.isEmpty()) {
                continue;
            }
            if(block.getIdentifier() != null && block.getComponent() != null) {
                blocks.add(block);
            } else {
                Logger.error("Incorrect NetBlock in " + component.getObject().getIdentifier() + " in component " + component.getClass().getName());
            }
        }
        for(NetBlock block : blocks) {
            relayConnection.sendBlock(block);
        }
    }

    @SaveState(name = "relayServerAddress")
    public String getRelayServerAddress() {
        return relayServerAddress;
    }

    @FromXML
    public void setRelayServerAddress(String address) {
        this.relayServerAddress = address;
    }

}
