package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.components.base.NetComponent;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.deserializers.NetDeserializer;
import pl.AWTGameEngine.objects.net.NetConnection;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.net.NetBlock;
import pl.AWTGameEngine.objects.net.StandardNetConnection;
import pl.AWTGameEngine.objects.transform.Vector3;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@ComponentGL
@DefaultComponent
@WebComponent
public class Client extends NetComponent {

    private StandardNetConnection netConnection;
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
        if(netConnection != null) {
            Logger.error("Client already connected.");
            return;
        }
        String address = ip + ":" + port;
        Logger.netInfo("Connecting to " + address + "...", false);
        try {
            this.netConnection = new StandardNetConnection(-1, new Socket(ip, port));
            handleConnection();
//            requestGameObject("player{id}", new Vector3(400, 400), new Vector3(100, 100), new Vector3());
//            requestComponent("player{id}", "pl.AWTGameEngine.components.BlankRenderer", "rgb(0, 200, 0)");
//            requestComponent("player{id}", "pl.AWTGameEngine.custom.Movement2D", "discover");
        } catch (IOException e) {
            Logger.exception("Cannot connect to " + address, e);
        }
    }

    public void disconnect() {
        if(netConnection == null) {
            return;
        }
        try {
            netConnection.close();
            netConnection = null;
            Logger.netInfo("Disconnected.", false);
        } catch (IOException e) {
            Logger.exception("Cannot disconnect!", e);
        }
    }

    private void handleConnection() {
        new Thread(() -> {
            while(netConnection.getSocket().isConnected()) {
                String response = "";
                try {
                    response = netConnection.getBufferedReader().readLine();
                    if(response == null) {
                        continue;
                    }
                    if(netConnection.getId() == -1) {
                        int id;
                        try {
                            id = Integer.parseInt(response);
                        } catch(NumberFormatException e) {
                            Logger.error("Cannot connect to server: " + response);
                            netConnection.getSocket().close();
                            netConnection = null;
                            return;
                        }
                        netConnection.updateId(id); // first response is an id
                        Logger.netInfo("\t\t-> Server assigned ID " + netConnection.getId() + " for me.", false);
                        Logger.netInfo("Connected.", false);
                        continue;
                    }
                    NetDeserializer.deserialize(getScene(), response, netConnection);
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

    @Override
    public void onNetUpdate() {
        if(this.netConnection == null) {
            return;
        }
        List<NetBlock> blocks = new ArrayList<>();
        for(ObjectComponent component : getScene().getSceneEventHandler().getComponents("onSynchronize")) {
            NetComponent netComponent = (NetComponent) component;
            if(component.getObject().getNet().getOwner() != netConnection.getId()) {
                continue;
            }
            if(!netComponent.canSynchronize()) {
                continue;
            }
            NetBlock block = netComponent.onSynchronize();
            if(block.getIdentifier() != null) {
                blocks.add(block);
            }
        }
        for(NetBlock block : blocks) {
            netConnection.sendBlock(block);
        }
        // synchronize position
        //todo: UDP instead of TCP
        blocks.clear();
        for(GameObject object : getScene().getGameObjects()) {
            if(object.getNet().getOwner() != netConnection.getId()) {
                continue;
            }
            NetBlock block = object.getNet().onPositionSynchronize();
            if(block.getIdentifier() != null) {
                blocks.add(block);
            }
        }
        for(NetBlock block : blocks) {
            netConnection.sendBlock(block);
        }
    }

    public void requestGameObject(String identifier, Vector3 position, Vector3 size, Vector3 rotation) {
        Logger.info("Requesting object...");
        netConnection.sendBlock(new NetBlock(identifier, null, position, size, rotation, netConnection.getId()));
    }

    public void requestComponent(String identifier, Class<? extends ObjectComponent> component, String data) {
        Logger.info("Requesting component...");
        netConnection.sendBlock(new NetBlock(identifier, component, data));
    }

    public NetConnection getConnectedClient() {
        return this.netConnection;
    }

    @FromXML
    public void setAutoConnect(String address) {
        this.autoConnectAddress = address;
    }

}