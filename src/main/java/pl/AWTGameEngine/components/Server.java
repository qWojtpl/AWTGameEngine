package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Component3D;
import pl.AWTGameEngine.annotations.DefaultComponent;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.annotations.WebComponent;
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

@Component3D
@DefaultComponent
@WebComponent
public class Server extends ObjectComponent {

    private int port = 5555;
    private final Thread tcpThread = new Thread(this::onTCPThreadUpdate);
    private final Thread udpThread = new Thread(this::onUDPThreadUpdate);
    private ServerSocket tcpSocket;
    private DatagramSocket udpSocket;
    private final List<Socket> sockets = new ArrayList<>();

    public Server(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        try {
            Logger.log(0, "Starting server on port " + port + "...");
            this.tcpSocket = new ServerSocket(port);
            tcpThread.start();
            Logger.log(0, "Server started.");
        } catch (IOException e) {
            Logger.log("Cannot start server", e);
        }
    }

    @Override
    public void onRemoveComponent() {
        try {
            for(Socket s : new ArrayList<>(sockets)) {
                s.close();
                sockets.remove(s);
            }
            Logger.log(0, "Server closed.");
            tcpSocket.close();
        } catch (IOException e) {
            Logger.log("Cannot close server socket!", e);
        }
    }

    private void onTCPThreadUpdate() {
        while(!tcpSocket.isClosed()) {
            try {
                Socket clientSocket = tcpSocket.accept();
                new Thread(() -> handleConnection(clientSocket), "SERVER-CLIENT-" + (sockets.size() + 1)).start();
            } catch (IOException e) {
                Logger.log("Server TCP exception", e);
            }
        }
    }

    private void handleConnection(Socket clientSocket) {
        sockets.add(clientSocket);
        int id = sockets.size();
        Logger.log(0, "Client " + tcpSocket.getInetAddress().getHostAddress() + " connected.");
        Logger.log(0, "\t\tAssigned new client with ID " + id);

        PrintWriter out;
        BufferedReader in;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            Logger.log("Error while initializing client connection with ID " + id, e);
            return;
        }

        Logger.log(0, "\t\tEstablished connection.");

        while (clientSocket.isConnected()) {
            try {
                Logger.log(0, "Received message: " + in.readLine());
            } catch (IOException e) {
                Logger.log("Error while receiving client " + id + " messages", e);
            }
        }

        try {
            out.close();
            in.close();
        } catch(IOException e) {
            Logger.log("Cannot close client " + id + " connection", e);
        }
    }

    private void onUDPThreadUpdate() {
        while(!udpSocket.isClosed()) {

            

        }
    }

    public void sendPacket(Socket socket, String message) {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, socket.getInetAddress(), socket.getPort());
        try {
            udpSocket.send(packet);
        } catch (IOException e) {
            Logger.log("Cannot send packet", e);
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
