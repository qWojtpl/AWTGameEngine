package pl.AWTGameEngine.components;

import io.github.erkko68.filament.LightManager;
import pl.AWTGameEngine.annotations.components.types.FilamentComponent;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.graphics.GraphicsManagerFilament;
import pl.AWTGameEngine.engine.panels.FilamentPanel;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.transform.Vector3;

@FilamentComponent
public class Light extends ObjectComponent {

    protected FilamentPanel panel;
    protected boolean lightBuilt = false;

    protected int entityId;

    protected Vector3 localPosition = new Vector3();

    // Light variables
    protected ColorObject color = new ColorObject("rgb(255,0,0)");
    protected float intensity = 1000000000;
    protected float falloff = 100f;

    public Light(GameObject object) {
        super(object);
    }

    @Override
    public void onFilamentInitialization() {
        this.panel = (FilamentPanel) getScene().getPanel();
        this.entityId = panel.getEngine().getEntityManager().create();
        panel.getFilamentScene().addEntity(entityId);
        createLight();
    }

    @Override
    public void onRemoveComponent() {
        lightBuilt = false;
        ((GraphicsManagerFilament) panel.getGraphicsManager3D()).disposeEntity(panel.getEngine(), entityId);
        panel.getEngine().getLightManager().destroy(entityId);
    }

    protected void createLight() {
        new LightManager.Builder(LightManager.Type.POINT)
                .castShadows(true)
                .shadowOptions(panel.getShadowOptions())
                .build(panel.getEngine(), entityId);

        lightBuilt = true;
        setLocalPosition(getLocalPosition());
        setColor(getColor());
        setIntensity(getIntensity());
        setFalloff(getFalloff());
    }

    private int getLightInstance() {
        return panel.getEngine().getLightManager().getInstance(entityId);
    }

    @Override
    public boolean onUpdatePosition(double newX, double newY, double newZ) {
        if(lightBuilt) {
            panel.getEngine().getLightManager().setPosition(getLightInstance(),
                    (float) (newX + localPosition.getX()),
                    (float) (newY + localPosition.getY()),
                    (float) (newZ + localPosition.getZ())
            );
        }
        return true;
    }

    // Variables

    @SaveState(name = "localPosition")
    public Vector3 getLocalPosition() {
        return this.localPosition;
    }

    @FromXML
    public void setLocalPosition(Vector3 localPosition) {
        this.localPosition = localPosition;
        localPosition.setNotifyAction((e) -> {
            onUpdatePosition(getObject().getPosition().getX(), getObject().getPosition().getY(), getObject().getPosition().getZ());
        });
        localPosition.getNotifyAction().accept(null);
    }

    @SaveState(name = "color")
    public ColorObject getColor() {
        return this.color;
    }

    @FromXML
    public void setColor(ColorObject color) {
        this.color = color;
        if(lightBuilt) {
            panel.getEngine().getLightManager().setColor(getLightInstance(),
                    color.getColor().getRed(),
                    color.getColor().getGreen(),
                    color.getColor().getBlue()
            );
        }
    }

    @SaveState(name = "intensity")
    public float getIntensity() {
        return this.intensity;
    }

    @FromXML
    public void setIntensity(float intensity) {
        this.intensity = intensity;
        if(lightBuilt) {
            panel.getEngine().getLightManager().setIntensity(getLightInstance(), intensity);
        }
    }

    @SaveState(name = "falloff")
    public float getFalloff() {
        return this.falloff;
    }

    @FromXML
    public void setFalloff(float falloff) {
        this.falloff = falloff;
        if(lightBuilt) {
            panel.getEngine().getLightManager().setFalloff(getLightInstance(), falloff);
        }
    }

}
