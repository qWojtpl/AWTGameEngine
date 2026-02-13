package pl.AWTGameEngine.engine.deserializers;

import pl.AWTGameEngine.components.Server;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.objects.ConnectedClient;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.scenes.Scene;

public class NetMessageDeserializer {

    public static void deserialize(Scene scene, String response, ConnectedClient client) {
        deserialize(scene, response, client, null);
    }

    public static void deserialize(Scene scene, String response, ConnectedClient client, Server server) {
        try {
            String[] split = response.split(";");
            if(split[0].contains("{id}") && server != null) { // if server is null then deserializing is executing on a client
                split[0] = split[0].replace("{id}", client.getId() + "");
            }
            GameObject object = scene.getGameObjectByName(split[0]);
            if(object == null) {
                if(server != null) {
                    if (!server.canClientsRequestObject()) {
                        Logger.error("Client " + client.getId() + " tried to request an object/component creation, but don't have permission for it.");
                        return;
                    }
                }
                Logger.warning(split[0] + " object not found, creating a new one...");
                object = scene.createGameObject(split[0]);
                if(server != null) {
                    object.getNet().setOwner(client.getId());
                    Logger.warning("Assigned ownership of " + split[0] + " to client " + client.getId());
                }
            }
            if(server != null) {
                if(object.getNet().getOwner() != client.getId()) {
                    Logger.error("Client " + client.getId() + " tried to change an object, but don't have permission for it.");
                    return;
                }
            }
            if("null".equals(split[1]) || split[1] == null) { // object-related synchronization instead of component-related
                object.getNet().onPositionSynchronizeReceived(split[2], server != null);
                return;
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
        } catch(Exception e) {
            Logger.exception("Cannot deserialize message", e);
        }
    }

}
