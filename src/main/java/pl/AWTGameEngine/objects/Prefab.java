package pl.AWTGameEngine.objects;

import pl.AWTGameEngine.components.base.ObjectComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Prefab {

    private final String identifier;
    private final List<PrefabComponent> components = new ArrayList<>();
    private final String externalPrefabPath;

    public Prefab(String identifier, String externalPrefabPath) {
        this.identifier = identifier;
        this.externalPrefabPath = externalPrefabPath;
    }

    public void addComponent(PrefabComponent prefabComponent) {
        if(!this.equals(prefabComponent.getPrefab())) {
            throw new RuntimeException("PrefabComponent is not owned by this prefab!");
        }
        components.add(prefabComponent);
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public List<PrefabComponent> getComponents() {
        return new ArrayList<>(this.components);
    }

    public String getExternalPrefabPath() {
        return this.externalPrefabPath;
    }

    public static class PrefabComponent {

        private final Prefab prefab;
        private final Class<? extends ObjectComponent> componentClass;
        private final HashMap<String, String> values;

        public PrefabComponent(Prefab prefab, Class<? extends ObjectComponent> componentClass, HashMap<String, String> values) {
            this.prefab = prefab;
            this.componentClass = componentClass;
            this.values = values;
        }

        public Prefab getPrefab() {
            return this.prefab;
        }

        public Class<? extends ObjectComponent> getComponentClass() {
            return this.componentClass;
        }

        public HashMap<String, String> getValues() {
            return this.values;
        }

    }

}
