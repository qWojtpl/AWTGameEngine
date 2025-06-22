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
import java.net.ServerSocket;
import java.net.Socket;

@Component3D
@DefaultComponent
@WebComponent
public class Server extends ObjectComponent {

    private int port = 5555;
    private final Thread serverThread = new Thread(this::onThreadUpdate);
    private boolean continueThread = true;
    private ServerSocket socket;

    public Server(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        try {
            Logger.log(0, "Starting server on port " + port + "...");
            this.socket = new ServerSocket(port);
            serverThread.start();
            Logger.log(0, "Server started.");
        } catch (IOException e) {
            Logger.log("Cannot start server", e);
        }
    }

    @Override
    public void onRemoveComponent() {
        try {
            continueThread = false;
            socket.close();
        } catch (IOException e) {
            Logger.log("Cannot close server socket!", e);
        }
    }

    private void onThreadUpdate() {
        while(continueThread) {
            try {
                Socket clientSocket = socket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                Logger.log(0, "Server received message: " + in.readLine());
                out.println("Received your message.");
                clientSocket.close();
                out.close();
                in.close();
            } catch (IOException e) {
                Logger.log("Server exception", e);
            }
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
