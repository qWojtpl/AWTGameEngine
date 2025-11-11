package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.*;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.deserializers.NetMessageDeserializer;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.NetBlock;
import pl.AWTGameEngine.objects.TransformSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@ComponentFX
@ComponentGL
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
        Logger.info("Connecting to " + address + "...");
        try {
            socket = new Socket(ip, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            handleConnection();
            Logger.info("Connected.");
            createGameObject("player{id}", new TransformSet(400, 400), new TransformSet(100, 100));
        } catch (IOException e) {
            Logger.exception("Cannot connect to " + address, e);
        }
    }

    public void disconnect() {
        if(socket == null) {
            return;
        }
        try {
            socket.close();
            Logger.info("Disconnected.");
        } catch (IOException e) {
            Logger.exception("Cannot disconnect!", e);
        }
    }

    private void handleConnection() {
        new Thread(() -> {
            while(socket.isConnected()) {
                String response = "";
                try {
                    response = in.readLine();
                    if(response == null) {
                        continue;
                    }
                    NetMessageDeserializer.deserialize(getScene(), response, socket);
                } catch (Exception e) {
                    Logger.exception("Cannot read a response (" + response + ")", e);
                }
            }
        }, "CLIENT-MESSAGE").start();
    }

    private void sendMessage(String message) {
        out.println(message);
    }

    private void sendNetBlock(NetBlock netBlock) {
        sendMessage(netBlock.formMessage());
    }

    public void createGameObject(String identifier, TransformSet position, TransformSet size) {
        Logger.info("Requesting object...");
        sendNetBlock(new NetBlock(identifier, null, position, size));
        sendNetBlock(new NetBlock(identifier, "pl.AWTGameEngine.components.BlankRenderer", "rgb(0, 200, 0)"));
    }

    @SerializationSetter
    public void setAutoConnect(String address) {
        this.autoConnectAddress = address;
    }

}