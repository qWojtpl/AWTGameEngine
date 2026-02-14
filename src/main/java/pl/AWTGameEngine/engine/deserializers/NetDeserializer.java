package pl.AWTGameEngine.engine.deserializers;

import pl.AWTGameEngine.components.Server;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.objects.ConnectedClient;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.scenes.Scene;

public class NetDeserializer {

    public static void deserialize(Scene scene, String response, ConnectedClient client) {
        deserialize(scene, response, client, null);
    }

    public static void deserialize(Scene scene, String response, ConnectedClient client, Server server) {
        try {
            String[] split = response.split(";");
            String objectId = split[0];
            String componentName = split[1];
            String data = split[2];
            // variables
            if(server != null) { // if server is null then deserializing is executing on a client
                objectId = parseVariables(objectId, client);
            }
            // create object
            GameObject object = createObject(objectId, scene, client, server);
            if(object == null) {
                return;
            }
            // synchronize object
            if("null".equals(componentName) || componentName == null) { // object-related synchronization instead of component-related
                object.getNet().onPositionSynchronizeReceived(data, server != null);
                return;
            }
            // create component
            ObjectComponent component = createComponent(componentName, object);
            if(component == null) {
                return;
            }
            // synchronize component
            component.onSynchronizeReceived(data);
        } catch(Exception e) {
            Logger.exception("Cannot deserialize message", e);
        }
    }

    private static String parseVariables(String input, ConnectedClient client) {
        if(input.contains("{id}")) {
            input.replace("{id}", String.valueOf(client.getId()));
        }
        return input;
    }

    private static GameObject createObject(String input, Scene scene, ConnectedClient client, Server server) {
        GameObject object = scene.getGameObjectByName(input);
        if(object == null) {
            if(server != null) {
                if(!server.canClientsRequestObject()) {
                    Logger.error("Client " + client.getId() + " tried to request an object/component creation, but don't have permission for it.");
                    return null;
                }
            }
            Logger.warning(input + " object not found, creating a new one...");
            object = scene.createGameObject(input);
            if(server != null) {
                object.getNet().setOwner(client.getId());
                Logger.warning("Assigned ownership of " + input + " to client " + client.getId());
            }
        }
        if(server != null) {
            if(object.getNet().getOwner() != client.getId()) {
                Logger.error("Client " + client.getId() + " tried to change an object, but don't have permission for it.");
                return null;
            }
        }
        return object;
    }

    private static ObjectComponent createComponent(String input, GameObject object) throws Exception {
        if(object == null) {
            return null;
        }
        Class<? extends ObjectComponent> clazz = Class.forName(input)
                .asSubclass(ObjectComponent.class);
        ObjectComponent component = object.getComponentByClass(clazz);
        //todo: many same components inside one object
        if(component == null) {
            Logger.warning(object.getIdentifier() + " object doesn't have component " + input + ", adding a new one...");
            component = clazz.getConstructor(GameObject.class).newInstance(object);
            object.addComponent(component);
        }
        return component;
    }

}
