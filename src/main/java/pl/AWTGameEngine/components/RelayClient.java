package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.components.base.NetComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.deserializers.NetDeserializer;
import pl.AWTGameEngine.engine.enums.DataType;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.net.ConnectedClient;

import java.net.Socket;

@DefaultComponent
@WebComponent
@ComponentGL
public class RelayClient extends NetComponent {

    private String relayServerAddress;
    private int autoConnect = -1;
    private ConnectedClient relayConnection;
    private int myId = -1;

    public RelayClient(GameObject object) {
        super(object);
    }

    @Override
    public void onSerializationFinish() {
        if(autoConnect == -1) {
            return;
        }
        connect(autoConnect);
    }

    public void connect(int sessionId) {
        if(relayServerAddress == null) {
            Logger.error("Cannot connect - relay server address is not set.");
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
        Logger.info("Sending request to " + relayServerAddress + " to connect to a session with id (" + sessionId + ")...");
        relayConnection.sendMessage("$connectTo:" + sessionId);
        Logger.info("Waiting for response...");
        String response;
        try {
            response = relayConnection.getBufferedReader().readLine();
        } catch(Exception e) {
            Logger.exception("Cannot read response", e);
            return;
        }
        if(response.equals("$sessionIncorrect")) {
            Logger.error("Session is incorrect!");
        } else if(response.equals("$sessionConnected")) {
            Logger.info("Connected to session (" + sessionId + ")!");
            new Thread(() -> {
                while(!relayConnection.getSocket().isClosed()) {
                    try {
                        String data = relayConnection.getBufferedReader().readLine();
                        if(myId == -1) {
                            myId = Integer.parseInt(data);
                            continue;
                        }
                        if(!data.startsWith("$")) {
                            NetDeserializer.deserialize(getScene(), data, new ConnectedClient(myId, null));
                        }
                    } catch(Exception e) {
                        Logger.exception("Exception while reading data from relay server", e);
                        // disconnect
                    }
                }
            }).start();
        }
    }

    @Override
    public void onNetUpdate() {

    }

    @SaveState(name = "autoConnect")
    public int getAutoConnect() {
        return this.autoConnect;
    }

    public void setAutoConnect(int autoConnect) {
        this.autoConnect = autoConnect;
    }

    @FromXML(type = DataType.NUMBER)
    public void setAutoConnect(String autoConnect) {
        setAutoConnect(Integer.parseInt(autoConnect));
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
