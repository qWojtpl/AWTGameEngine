package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.components.types.ComponentFX;
import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.deserializers.NetMessageDeserializer;
import pl.AWTGameEngine.objects.ConnectedClient;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.NetBlock;
import pl.AWTGameEngine.objects.TransformSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@ComponentFX
@ComponentGL
@DefaultComponent
@WebComponent
public class Client extends ObjectComponent {

    private ConnectedClient connectedClient;
    private String autoConnectAddress = null;

    public Client(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        if(autoConnectAddress != null) {
            String[] split = autoConnectAddress.split(":");
            connect(split[0], Integer.parseInt(split[1]));
        }
    }

    @Override
    public void onRemoveComponent() {
        disconnect();
    }

    public void connect(String ip, int port) {
        if(connectedClient != null) {
            Logger.error("Client already connected.");
            return;
        }
        String address = ip + ":" + port;
        Logger.info("Connecting to " + address + "...");
        try {
            this.connectedClient = new ConnectedClient(-1, new Socket(ip, port));
            handleConnection();
            Logger.info("Connected.");
            requestGameObject("player{id}", new TransformSet(400, 400), new TransformSet(100, 100), new TransformSet());
            requestComponent("player{id}", "pl.AWTGameEngine.components.BlankRenderer", "rgb(0, 200, 0)");
            requestComponent("player{id}", "pl.AWTGameEngine.custom.Movement2D", "discover");
        } catch (IOException e) {
            Logger.exception("Cannot connect to " + address, e);
        }
    }

    public void disconnect() {
        if(connectedClient == null) {
            return;
        }
        try {
            connectedClient.close();
            connectedClient = null;
            Logger.info("Disconnected.");
        } catch (IOException e) {
            Logger.exception("Cannot disconnect!", e);
        }
    }

    private void handleConnection() {
        new Thread(() -> {
            while(connectedClient.getSocket().isConnected()) {
                String response = "";
                try {
                    response = connectedClient.getBufferedReader().readLine();
                    if(response == null) {
                        continue;
                    }
                    if(connectedClient.getId() == -1) {
                        connectedClient.updateId(Integer.parseInt(response)); // first response is an id
                        Logger.info("\t\t-> Server assigned ID " + connectedClient.getId() + " for me.");
                        continue;
                    }
                    NetMessageDeserializer.deserialize(getScene(), response, connectedClient);
                } catch (Exception e) {
                    if(response.isEmpty()) {
                        Logger.error("Server closed a connection.");
                        disconnect();
                        return;
                    }
                    Logger.exception("Cannot read a response (" + response + ")", e);
                }
            }
        }, "CLIENT-MESSAGE").start();
    }

    private void sendNetBlock(NetBlock netBlock) {
        connectedClient.sendMessage(netBlock.formMessage());
    }

    @Override
    public void onNetUpdate() {
        List<NetBlock> blocks = new ArrayList<>();
        for(ObjectComponent component : getScene().getSceneEventHandler().getComponents("onSynchronize")) {
            if(component.getObject().getNet().getOwner() != connectedClient.getId()) {
                continue;
            }
            if(!component.canSynchronize()) {
                continue;
            }
            NetBlock block = component.onSynchronize();
            if(block.getIdentifier() != null) {
                blocks.add(block);
            }
        }
        for(NetBlock block : blocks) {
            sendNetBlock(block);
        }
        // synchronize position
        //todo: UDP instead of TCP
        blocks.clear();
        for(GameObject object : getScene().getGameObjects()) {
            if(object.getNet().getOwner() != connectedClient.getId()) {
                continue;
            }
            NetBlock block = object.getNet().onPositionSynchronize();
            if(block.getIdentifier() != null) {
                blocks.add(block);
            }
        }
        for(NetBlock block : blocks) {
            sendNetBlock(block);
        }
    }

    public void requestGameObject(String identifier, TransformSet position, TransformSet size, TransformSet rotation) {
        Logger.info("Requesting object...");
        sendNetBlock(new NetBlock(identifier, null, position, size, rotation, connectedClient.getId()));
    }

    public void requestComponent(String identifier, String component, String data) {
        Logger.info("Requesting component...");
        sendNetBlock(new NetBlock(identifier, component, data));
    }

    @FromXML
    public void setAutoConnect(String address) {
        this.autoConnectAddress = address;
    }

}