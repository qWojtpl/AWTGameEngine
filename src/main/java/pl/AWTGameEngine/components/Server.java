package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.components.types.ComponentFX;
import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.deserializers.NetDeserializer;
import pl.AWTGameEngine.objects.ConnectedClient;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.NetBlock;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ComponentFX
@ComponentGL
@DefaultComponent
@WebComponent
public class Server extends ObjectComponent {

    private int port = 5555;
    private final Thread tcpThread = new Thread(this::acceptClient);
    private final Thread udpThread = new Thread(this::onUDPThreadUpdate);
    private ServerSocket tcpSocket;
    private DatagramSocket udpSocket;

    // clients
    private final HashMap<Integer, ConnectedClient> connectedClients = new HashMap<>();
    private int currentId = 0;

    public Server(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        try {
            Logger.info("Starting server on port " + port + "...");
            this.tcpSocket = new ServerSocket(port);
            tcpThread.start();
            Logger.info("Server started.");
        } catch (IOException e) {
            Logger.exception("Cannot start server", e);
        }
    }

    @Override
    public void onRemoveComponent() {
        Logger.info("Closing server...");
        try {
            for(ConnectedClient client : new ArrayList<>(connectedClients.values())) {
                disconnect(client);
            }
            Logger.info("Server closed.");
            tcpSocket.close();
        } catch (IOException e) {
            Logger.exception("Cannot close server socket!", e);
        }
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
        for(ConnectedClient client : connectedClients.values()) {
            for(NetBlock block : blocks) {
                client.sendBlock(block);
            }
        }
    }

    private void sendComponents() {
        List<NetBlock> blocks = new ArrayList<>();
        for(ObjectComponent component : getScene().getSceneEventHandler().getComponents("onSynchronize")) {
            if(!component.canSynchronize()) {
                continue;
            }
            NetBlock block = component.onSynchronize();
            if(block.isEmpty()) {
                continue;
            }
            if(block.getIdentifier() != null && block.getComponent() != null) {
                blocks.add(block);
            } else {
                Logger.error("Incorrect NetBlock in " + component.getObject().getIdentifier() + " in component " + component.getClass().getName());
            }
        }
        for(ConnectedClient client : connectedClients.values()) {
            for(NetBlock block : blocks) {
                client.sendBlock(block);
            }
        }
    }

    private void acceptClient() {
        while(!tcpSocket.isClosed()) {
            try {
                Socket clientSocket = tcpSocket.accept();
                new Thread(() -> handleConnection(clientSocket), "SERVER-CLIENT-" + (connectedClients.size() + 1)).start();
            } catch (IOException e) {
                Logger.exception("Server TCP exception", e);
            }
        }
    }

    private void handleConnection(Socket clientSocket) {
        int id = currentId++;
        Logger.info("Client " + getClientAddress(clientSocket) + " connected.");
        Logger.info("\t\t-> Assigned new client to ID " + id);

        ConnectedClient connectedClient;
        try {
            connectedClient = new ConnectedClient(id, clientSocket);
            connectedClient.sendInitMessage();
        } catch (IOException e) {
            Logger.exception("Error while initializing client connection with ID " + id, e);
            return;
        }

        connectedClients.put(id, connectedClient);
        Logger.info("\t\t-> Established connection.");

        // prepare objects to be sent to the client during next update

        for(GameObject object : getScene().getGameObjects()) {
            object.getNet().clearCache();
        }

        for(ObjectComponent component : getScene().getSceneEventHandler().getComponents("onSynchronize")) {
            component.clearNetCache();
        }

        for(ObjectComponent component : getScene().getSceneEventHandler().getComponents("onClientDisconnect#Server#ConnectedClient")) {
            component.onClientConnect(this, connectedClient);
        }

        while (connectedClient.getSocket().isConnected()) {
            try {
                String message = connectedClient.getBufferedReader().readLine();
                if(message == null) {
                    continue;
                }
                NetDeserializer.deserialize(getScene(), message, connectedClient, this);
            } catch (IOException e) {
                disconnect(connectedClient);
                Logger.exception("Exception while receiving a message. Had to disconnect a client.", e);
                return;
            }
        }

        disconnect(connectedClient);
    }

    private void onUDPThreadUpdate() {
/*        while(!udpSocket.isClosed()) {

        }*/
    }

    public void disconnect(ConnectedClient client) {
        try {
            Logger.info("Client " + getClientAddress(client.getSocket()) +
                    " (ID " + client.getId() + ") disconnected.");
            for(ObjectComponent component : getScene().getSceneEventHandler().getComponents("onClientDisconnect#Server#ConnectedClient")) {
                component.onClientDisconnect(this, client);
            }
            client.close();
            connectedClients.remove(client.getId());
        } catch(IOException e) {
            Logger.exception("Cannot disconnect client", e);
        }
    }

    private String getClientAddress(Socket clientSocket) {
        return clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
    }

    public void sendPacket(Socket socket, String message) {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, socket.getInetAddress(), socket.getPort());
        try {
            udpSocket.send(packet);
        } catch (IOException e) {
            Logger.exception("Cannot send packet", e);
        }
    }

    public ConnectedClient getClientById(int id) {
        return this.connectedClients.getOrDefault(id, null);
    }

    public boolean canClientsRequestObject() {
        return false;
    }

    @FromXML
    public void setPort(String port) {
        this.port = Integer.parseInt(port);
    }

    @FromXML
    public void setMaxClients(String clients) {

    }

}
