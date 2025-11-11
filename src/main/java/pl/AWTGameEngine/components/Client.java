package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.*;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.objects.GameObject;

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
                    String[] split = response.split(";");
                    GameObject object = getScene().getGameObjectByName(split[0]);
                    if(object == null) {
                        Logger.warning(split[0] + " object not found, creating a new one...");
                        object = getScene().createGameObject(split[0]);
                    }
                    if("null".equals(split[1]) || split[1] == null) { // object-related synchronization instead of component-related
                        object.onPositionSynchronizeReceived(split[2]);
                        continue;
                    }
                    Class<? extends ObjectComponent> clazz = Class.forName(split[1])
                            .asSubclass(ObjectComponent.class);
                    ObjectComponent component = object.getComponentByClass(clazz);
                    //todo: many same components inside one object
                    if(component == null) {
                        Logger.warning(split[0] + " object doesn't have component " + split[1] + ", adding a new one...");
                        component = clazz.getConstructor(GameObject.class).newInstance(object);
                        object.addComponent(component);
                    }
                    component.onSynchronizeReceived(split[2]);
                } catch (Exception e) {
                    Logger.exception("Cannot read a response (" + response + ")", e);
                }
            }
        }, "CLIENT-MESSAGE").start();
    }

    private void sendMessage(String message) {
        out.println(message);
    }

    @SerializationSetter
    public void setAutoConnect(String address) {
        this.autoConnectAddress = address;
    }

}