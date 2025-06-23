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
import java.net.Socket;

@Component3D
@DefaultComponent
@WebComponent
public class Client extends ObjectComponent {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
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
        String address = ip + ":" + port;
        Logger.log(0, "Connecting to " + address + "...");
        try {
            socket = new Socket(ip, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sendMessage("Hello!", true);
        } catch (IOException e) {
            Logger.log("Cannot connect to " + address, e);
        }
    }

    public void disconnect() {
        try {
            socket.close();
            Logger.log(0, "Disconnected.");
        } catch (IOException e) {
            Logger.log("Cannot disconnect!", e);
        }
    }

    private void sendMessage(String message, boolean b) {
        new Thread(() -> {
            Logger.log(0, "Sending message: " + message);
            out.println(message);
            String response = null;
            try {
                response = in.readLine();
            } catch (IOException e) {
                Logger.log("Cannot read a response", e);
            }
            Logger.log(0, "Server responded with " + response);
            if(b) sendMessage("t2", false);
        }, "CLIENT-MESSAGE").start();
    }

    @SerializationSetter
    public void setAutoConnect(String address) {
        this.autoConnectAddress = address;
    }

}
