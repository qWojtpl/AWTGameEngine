package pl.AWTGameEngine.objects;

import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.components.WebHandler;
import pl.AWTGameEngine.engine.*;
import pl.AWTGameEngine.engine.helpers.RotationHelper;
import pl.AWTGameEngine.scenes.Scene;

import java.util.*;
import java.util.List;

public class GameObject {

    private final String identifier;
    private final Scene scene;
    private boolean active = true;
    private TransformSet position = new TransformSet(0, 0, 0);
    private TransformSet size = new TransformSet(0, 0, 0);
    private TransformSet rotation = new TransformSet(0, 0, 0);
    private QuaternionTransformSet quaternionRotation = new QuaternionTransformSet(0, 0, 0, 0);
    private Net net;
    private final EventHandler eventHandler = new EventHandler();
    private final List<ObjectComponent> components = new ArrayList<>();

    public GameObject(String identifier, Scene scene) {
        this.identifier = identifier;
        this.scene = scene;
        if(RenderEngine.WEB.equals(getScene().getRenderEngine())) {
            this.addComponent(new WebHandler(this));
        }
    }

    public void addComponent(ObjectComponent component) {
        RenderEngine renderEngine = scene.getRenderEngine();
        if(RenderEngine.WEB.equals(renderEngine)) {
            if(!component.isWebComponent()) {
                Logger.error("Component " + component.getComponentName() +
                        " cannot be added to " + identifier + " because is not marked as WebComponent");
                return;
            }
        } else if(RenderEngine.DEFAULT.equals(renderEngine)) {
            if(!component.isDefaultComponent()) {
                Logger.error("Component " + component.getComponentName() +
                        " cannot be added to " + identifier + " because is not marked as DefaultComponent");
                return;
            }
        } else if(RenderEngine.FX3D.equals(renderEngine)) {
            if(!component.isFXComponent()) {
                Logger.error("Component " + component.getComponentName() +
                        " cannot be added to " + identifier + " because is not marked as ComponentFX");
                return;
            }
        }  else if(RenderEngine.OPENGL.equals(renderEngine)) {
            if(!component.isGLComponent()) {
                Logger.error("Component " + component.getComponentName() +
                        " cannot be added to " + identifier + " because is not marked as ComponentGL");
                return;
            }
        }
        if(component.isUnique() && !getComponentsByClass(component.getClass()).isEmpty()) {
            Logger.error("Component " + component.getClass().getName() + " is unique, cannot add another!");
            return;
        }
        if(component.hasRequiredClass()) {
            Class<? extends ObjectComponent> requiredClass = component.getRequiredClass();
            boolean found = false;
            for(ObjectComponent c : components) {
                if(c.getClass().isInstance(requiredClass) || c.getClass().equals(requiredClass)) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                Logger.error("Cannot add component " + component.getClass().getName() + ", because it requires " + requiredClass + " component!");
                return;
            }
        }
        if(!component.getObject().equals(this)) {
            Logger.error("Component " + component.getClass().getName() + " object is wrong!");
            return;
        }
        for(ObjectComponent c : getComponents()) {
            if(c.conflictsWith(component.getClass()) || component.conflictsWith(c.getClass())) {
                Logger.error("GameObject: " + getIdentifier() + "\nObject components: " + getComponents() + "\n" +
                                "Component " + c.getClass().getName() + " has conflict with "
                                + component.getClass().getName() + "; cannot add component " + component.getClass().getName());
                return;
            }
        }
        this.components.add(component);
        eventHandler.registerComponent(component);
        getScene().getSceneEventHandler().registerComponent(component);
        component.onAddComponent();
        for(ObjectComponent c : eventHandler.getComponents("onAddComponent#ObjectComponent")) {
            if(c == component) {
                continue;
            }
            c.onAddComponent(component);
        }
    }

    public void removeComponent(ObjectComponent component) {
        this.components.remove(component);
        eventHandler.removeComponent(component);
        getScene().getSceneEventHandler().removeComponent(component);
        component.onRemoveComponent();
    }

    public boolean hasComponent(Class<? extends ObjectComponent> component) {
        return getComponentsByClass(component).isEmpty();
    }

    public void moveX(double x) {
        double delta = x - this.position.getX();
        if(delta == 0) {
            return;
        }
        double direction = delta < 0 ? -1 : 1;
        for(double i = 0; i < Math.abs(delta); i++) {
            if(tryMoveX(direction)) {
                setX(this.position.getX() + direction);
                continue;
            }
            return;
        }
    }

    public boolean tryMoveX(double direction) {
        for(ObjectComponent component : getComponents()) {
            if(!component.onUpdatePosition(this.position.getX() + direction, this.position.getY())) {
                return false;
            }
        }
        return true;
    }

    public void moveY(double y) {
        double delta = y - this.position.getY();
        if(delta == 0) {
            return;
        }
        double direction = delta < 0 ? -1 : 1;
        for(double i = 0; i < Math.abs(delta); i++) {
            if(tryMoveY(direction)) {
                setY(this.position.getY() + direction);
                continue;
            }
            return;
        }
    }

    public boolean tryMoveY(double direction) {
        for(ObjectComponent component : getComponents()) {
            if(!component.onUpdatePosition(this.position.getX(), this.position.getY() + direction)) {
                return false;
            }
        }
        return true;
    }

    public void rotate(double angle) {
        double delta = angle - rotation.getX();
        if(delta == 0) {
            return;
        }
        double direction = delta < 0 ? -1 : 1;
        for(double i = 0; i < Math.abs(delta); i++) {
            if(tryRotate(direction)) {
                setRotation(new TransformSet(rotation.getX() + direction, rotation.getY(), rotation.getZ()));
                continue;
            }
            return;
        }
    }

    public boolean tryRotate(double direction) {
        for(ObjectComponent component : getComponents()) {
            if(!component.onUpdateRotation(this.rotation.getX() + direction)) {
                return false;
            }
        }
        return true;
    }

    @SaveState(name = "id")
    public String getIdentifier() {
        return this.identifier;
    }

    public Scene getScene() {
        return this.scene;
    }

    public List<ObjectComponent> getComponents() {
        return new ArrayList<>(this.components);
    }

    public List<ObjectComponent> getComponentsByClass(Class<? extends ObjectComponent> clazz) {
        List<ObjectComponent> componentList = new ArrayList<>();
        for(ObjectComponent component : components) {
            if(clazz.isInstance(component)) {
                componentList.add(component);
            }
        }
        return componentList;
    }

    public ObjectComponent getComponentByClass(Class<? extends ObjectComponent> clazz) {
        List<ObjectComponent> componentList = getComponentsByClass(clazz);
        if(componentList.isEmpty()) {
            return null;
        }
        return componentList.get(0);
    }

    public boolean isActive() {
        return this.active;
    }

    public double getX() {
        return this.position.getX();
    }

    public double getY() {
        return this.position.getY();
    }

    public double getZ() {
        return this.position.getZ();
    }

    @SaveState(name = "position")
    public TransformSet getPosition() {
        return this.position.clone();
    }

    @SaveState(name = "rotation")
    public TransformSet getRotation() {
        if(this.rotation == null) {
            double[] pos = RotationHelper.quaternionToEulerXYZ(
                    quaternionRotation.getX(),
                    quaternionRotation.getY(),
                    quaternionRotation.getZ(),
                    quaternionRotation.getW()
            );
            this.rotation = new TransformSet(pos[0], pos[1], pos[2]);
        }
        return this.rotation.clone();
    }

    public QuaternionTransformSet getQuaternionRotation() {
        if(this.quaternionRotation == null) {
            double[] pos = RotationHelper.xyzEulerToQuaternion(
                    rotation.getX(),
                    rotation.getY(),
                    rotation.getZ()
            );
            this.quaternionRotation = new QuaternionTransformSet(pos[0], pos[1], pos[2], pos[3]);
        }
        return this.quaternionRotation;
    }

    public double getCenterX() {
        return getX() + getSizeX() / 2;
    }

    public double getCenterY() {
        return getY() + getSizeY() / 2;
    }

    public double getSizeX() {
        return this.size.getX();
    }

    public double getSizeY() {
        return this.size.getY();
    }

    @SaveState(name = "size")
    public TransformSet getSize() {
        return this.size.clone();
    }

    public EventHandler getEventHandler() {
        return this.eventHandler;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setX(double x) {
        double delta = x - this.position.getX();
        if(delta == 0) {
            return;
        }
        this.position.setX(x);
        for(ObjectComponent component : eventHandler.getComponents("onUpdatePosition#double#double")) {
            component.onUpdatePosition(x, this.position.getY());
        }
        for(ObjectComponent component : eventHandler.getComponents("onUpdatePosition#double#double#double")) {
            component.onUpdatePosition(x, this.position.getY(), this.position.getZ());
        }
    }

    public void setX(String x) {
        setX(Integer.parseInt(x));
    }

    public void setY(double y) {
        double delta = y - this.position.getY();
        if(delta == 0) {
            return;
        }
        this.position.setY(y);
        for(ObjectComponent component : eventHandler.getComponents("onUpdatePosition#double#double")) {
            component.onUpdatePosition(this.position.getX(), y);
        }
        for(ObjectComponent component : eventHandler.getComponents("onUpdatePosition#double#double#double")) {
            component.onUpdatePosition(this.position.getX(), y, this.position.getZ());
        }
    }

    public void setY(String y) {
        setY(Integer.parseInt(y));
    }

    public void setZ(double z) {
        double delta = z - this.position.getZ();
        if(delta == 0) {
            return;
        }
        this.position.setZ(z);
        for(ObjectComponent component : eventHandler.getComponents("onUpdatePosition#double#double#double")) {
            component.onUpdatePosition(this.position.getX(), this.position.getY(), z);
        }
    }

    public void setZ(String z) {
        setZ(Integer.parseInt(z));
    }

    public void setPosition(TransformSet transform) {
        this.position = transform.clone();
        for(ObjectComponent component : eventHandler.getComponents("onUpdatePosition#double#double")) {
            component.onUpdatePosition(this.position.getX(), this.position.getY());
        }
        for(ObjectComponent component : eventHandler.getComponents("onUpdatePosition#double#double#double")) {
            component.onUpdatePosition(this.position.getX(), this.position.getY(), this.position.getZ());
        }
    }

    public void setRotation(TransformSet transform) {
        this.rotation = transform.clone();
        this.quaternionRotation = null;
        for(ObjectComponent component : eventHandler.getComponents("onUpdateRotation")) {
            component.onUpdateRotation();
        }
    }

    public void setQuaternionRotation(QuaternionTransformSet transform, ObjectComponent blockNotify) {
        this.rotation = null;
        this.quaternionRotation = transform.clone();
        for(ObjectComponent component : eventHandler.getComponents("onUpdateRotation")) {
            if(component.equals(blockNotify)) {
                continue;
            }
            component.onUpdateRotation();
        }
    }
    
    public void setQuaternionRotation(QuaternionTransformSet transform) {
        setQuaternionRotation(transform, null);
    } 

    public void setSizeX(double x) {
        this.size.setX(x);
        for(ObjectComponent component : eventHandler.getComponents("onUpdateSize#double#double")) {
            component.onUpdateSize(x, this.size.getY());
        }
        for(ObjectComponent component : eventHandler.getComponents("onUpdateSize#double#double#double")) {
            component.onUpdateSize(x, this.size.getY(), this.size.getZ());
        }
    }

    public void setSizeX(String x) {
        setSizeX(Integer.parseInt(x));
    }

    public void setSizeY(double y) {
        this.size.setY(y);
        for(ObjectComponent component : eventHandler.getComponents("onUpdateSize#double#double")) {
            component.onUpdateSize(this.size.getX(), y);
        }
        for(ObjectComponent component : eventHandler.getComponents("onUpdateSize#double#double#double")) {
            component.onUpdateSize(this.size.getX(), y, this.size.getZ());
        }
    }

    public void setSizeY(String y) {
        setSizeY(Integer.parseInt(y));
    }

    public void setSizeZ(double z) {
        this.size.setZ(z);
        for(ObjectComponent component : eventHandler.getComponents("onUpdateSize#double#double#double")) {
            component.onUpdateSize(this.size.getX(), this.size.getY(), z);
        }
    }

    public void setSizeZ(String z) {
        setSizeZ(Integer.parseInt(z));
    }

    public void setSize(TransformSet transform) {
        this.size = transform.clone();
        for(ObjectComponent component : eventHandler.getComponents("onUpdateSize#double#double")) {
            component.onUpdateSize(this.size.getX(), this.size.getY());
        }
        for(ObjectComponent component : eventHandler.getComponents("onUpdateSize#double#double#double")) {
            component.onUpdateSize(this.size.getX(), this.size.getY(), this.size.getZ());
        }      
    }

    public Net getNet() {
        if(this.net == null) {
            this.net = new Net();
        }
        return this.net;
    }

    public class Net {

        private TransformSet cachedPosition = null;
        private TransformSet cachedSize = null;
        private QuaternionTransformSet cachedRotation = null;
        private int owner = -1;

        public final NetBlock onPositionSynchronize() {
            if(getPosition().equals(cachedPosition) && getSize().equals(cachedSize) && getQuaternionRotation().equals(cachedRotation)) {
                return new NetBlock();
            }
            cachedPosition = getPosition();
            cachedSize = getSize();
            cachedRotation = getQuaternionRotation();
            return new NetBlock(
                    getIdentifier(),
                    null, // null component points to GameObject synchronization
                    cachedPosition,
                    cachedSize,
                    cachedRotation,
                    owner
            );
        }

        public final void onPositionSynchronizeReceived(String data, boolean server) {
            String[] split = data.split("╚");
            TransformSet newPosition = new TransformSet().deserializeFromToString(split[0]);
            TransformSet newSize = new TransformSet().deserializeFromToString(split[1]);
            QuaternionTransformSet newRotation = new QuaternionTransformSet().deserializeFromToString(split[2]);
            if(!newPosition.equals(cachedPosition)) {
                // (cl) create -> (srv) received -> (srv) send new -> server don't want a cache to exist in onPositionSynchronize,
                // because it would be blocked, so if it's a new object, cache won't be initialized here
                if(cachedPosition != null) {
                    cachedPosition = newPosition;
                }
                setPosition(newPosition);
            }
            if(!newSize.equals(cachedSize)) {
                if(cachedSize != null) {
                    cachedSize = newSize;
                }
                setSize(newSize);
            }
            if(!newRotation.equals(cachedRotation)) {
                if(cachedRotation != null) {
                    cachedRotation = newRotation;
                }
                setQuaternionRotation(newRotation);
            }
            if(!server) {
                setOwner(Integer.parseInt(split[3]));
            }
        }

        public void clearCache() {
            cachedPosition = null;
            cachedSize = null;
            cachedRotation = null;
        }

        public int getOwner() {
            return this.owner;
        }

        public void setOwner(int owner) {
            this.owner = owner;
        }

    }

}