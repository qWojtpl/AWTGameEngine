package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.*;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.objects.GameObject;

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
    private final Thread tcpThread = new Thread(this::onTCPThreadUpdate);
    private final Thread udpThread = new Thread(this::onUDPThreadUpdate);
    private ServerSocket tcpSocket;
    private DatagramSocket udpSocket;

    // clients
    private final List<Socket> sockets = new ArrayList<>();
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

    private void onTCPThreadUpdate() {
        while(!tcpSocket.isClosed()) {
            try {
                Socket clientSocket = tcpSocket.accept();
                new Thread(() -> handleConnection(clientSocket), "SERVER-CLIENT-" + (sockets.size() + 1)).start();
            } catch (IOException e) {
                Logger.exception("Server TCP exception", e);
            }
        }
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
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            Logger.exception("Error while initializing client connection with ID " + id, e);
            return;
        }

        out.println(id);
        Logger.info("\t\t-> Established connection.");

        while (clientSocket.isConnected()) {
            try {
                Logger.info("Received message: " + in.readLine());
            } catch (IOException e) {
                disconnect(clientSocket);
                break;
            }
        }

        try {
            out.close();
            in.close();
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
