package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.*;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.NetBlock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
    private final List<Socket> sockets = new ArrayList<>();
    private final HashMap<Socket, PrintWriter> writers = new HashMap<>();
    private final HashMap<Socket, Integer> clientIds = new HashMap<>();
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
        try {
            for(Socket s : new ArrayList<>(sockets)) {
                s.close();
                sockets.remove(s);
            }
            Logger.info("Server closed.");
            tcpSocket.close();
        } catch (IOException e) {
            Logger.exception("Cannot close server socket!", e);
        }
    }

    @Override
    public void onUpdate() {
        List<NetBlock> blocks = new ArrayList<>();
        for(ObjectComponent component : getScene().getSceneEventHandler().getComponents("onSynchronize")) {
            NetBlock block = component.onSynchronize();
            if(block.getIdentifier() != null) {
                blocks.add(block);
            }
        }
        for(Socket socket : sockets) {
            for(NetBlock block : blocks) {
                sendTCPMessage(socket, block.formMessage());
            }
        }
        // synchronize position
        //todo: UDP instead of TCP
        blocks.clear();
        for(GameObject object : getScene().getGameObjects()) {
            blocks.add(object.onPositionSynchronize());
        }
        for(Socket socket : sockets) {
            for(NetBlock block : blocks) {
                sendTCPMessage(socket, block.formMessage());
            }
        }
    }

    private void acceptClient() {
        while(!tcpSocket.isClosed()) {
            try {
                Socket clientSocket = tcpSocket.accept();
                new Thread(() -> handleConnection(clientSocket), "SERVER-CLIENT-" + (sockets.size() + 1)).start();
            } catch (IOException e) {
                Logger.exception("Server TCP exception", e);
            }
        }
    }

    private void sendTCPMessage(Socket client, String message) {
        writers.get(client).println(message);
    }

    private void handleConnection(Socket clientSocket) {
        sockets.add(clientSocket);
        Logger.info("Client " + getClientAddress(clientSocket) + " connected.");

        int id = currentId++;
        clientIds.put(clientSocket, id);
        Logger.info("\t\t-> Assigned new client to ID " + id);

        PrintWriter out;
        BufferedReader in;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            writers.put(clientSocket, out);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            Logger.exception("Error while initializing client connection with ID " + id, e);
            return;
        }

        Logger.info("\t\t-> Established connection.");

        while (clientSocket.isConnected()) {
            try {
                String message = in.readLine();
                if(message == null) {
                    continue;
                }
                Logger.info("Received message: " + message);
            } catch (IOException e) {
                Logger.exception("Exception while receiving a message. Have to disconnect a client.", e);
                disconnect(clientSocket);
                break;
            }
        }

        try {
            out.close();
            in.close();
            writers.remove(clientSocket);
        } catch(IOException e) {
            Logger.exception("Cannot close client " + id + " connection", e);
        }
    }

    private void onUDPThreadUpdate() {
        while(!udpSocket.isClosed()) {

            

        }
    }

    public void disconnect(Socket clientSocket) {
        try {
            Logger.info("Client " + getClientAddress(clientSocket) +
                    " (ID " + clientIds.get(clientSocket) + ") disconnected.");
            clientSocket.close();
            sockets.remove(clientSocket);
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

    @SerializationSetter
    public void setPort(String port) {
        this.port = Integer.parseInt(port);
    }

    @SerializationSetter
    public void setSyncList(String syncList) {

    }

    @SerializationSetter
    public void setMaxClients(String clients) {

    }

}
