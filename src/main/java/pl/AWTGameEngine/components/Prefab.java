package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.components.types.*;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.components.base.NetComponent;
import pl.AWTGameEngine.engine.deserializers.PrefabDeserializer;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.net.NetBlock;

/**
 * Injects a prefab to an object
 */
@DefaultComponent
@WebComponent
@ComponentGL
public class Prefab extends NetComponent {

    private String prefabName;

    public Prefab(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        PrefabDeserializer.injectPrefab(getScene().getPrefab(prefabName), getObject(), false);
        getObject().removeComponent(this);
    }

    @SaveState(name = "prefabName")
    public String getPrefabName() {
        return this.prefabName;
    }

    @FromXML
    public void setPrefabName(String prefabName) {
        this.prefabName = prefabName;
    }

    @Override
    public NetBlock onSynchronize() {
        return new NetBlock(getObject().getIdentifier(), Prefab.class, "");
    }

    @Override
    public void onSynchronizeReceived(String data) {
        this.prefabName = data;
    }

}
