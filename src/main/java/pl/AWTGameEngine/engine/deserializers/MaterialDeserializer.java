package pl.AWTGameEngine.engine.deserializers;

import org.w3c.dom.Node;
import pl.AWTGameEngine.objects.render.Material;
import pl.AWTGameEngine.scenes.Scene;

public class MaterialDeserializer {

    public static void deserialize(String name, Scene scene, Node node) {
        Material material = new Material(name);
        for(int i = 0; i < node.getAttributes().getLength(); i++) {
            String fieldName = node.getAttributes().item(i).getNodeName();
            if(fieldName.equalsIgnoreCase("name")) {
                continue;
            }
            String value = node.getAttributes().item(i).getNodeValue();
            String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            XMLDeserializer.getInstance().handleSetMethod(material, methodName, value);
        }
        scene.addMaterial(material);
    }

}
