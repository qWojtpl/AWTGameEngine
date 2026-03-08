package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.components.types.*;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.components.base.NetComponent;
import pl.AWTGameEngine.engine.deserializers.PrefabDeserializer;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.NetBlock;

/**
 * Injects a prefab to an object
 */
@DefaultComponent
@WebComponent
@ComponentFX
@ComponentGL
public class Prefab extends NetComponent {

    private String prefabName;

    public Prefab(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        PrefabDeserializer.injectPrefab(getScene().getPrefab(prefabName), getObject(), false);
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
        return new NetBlock(getObject().getIdentifier(), getComponentName(), "");
    }

    @Override
    public void onSynchronizeReceived(String data) {
        this.prefabName = data;
    }

}
