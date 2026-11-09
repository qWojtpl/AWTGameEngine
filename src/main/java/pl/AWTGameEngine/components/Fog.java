package pl.AWTGameEngine.components;

import io.github.erkko68.filament.View;
import pl.AWTGameEngine.annotations.components.types.FilamentComponent;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.panels.FilamentPanel;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.GameObject;

@FilamentComponent
public class Fog extends ObjectComponent {

    private final View.FogOptions fogOptions = new View.FogOptions();

    public Fog(GameObject object) {
        super(object);
        fogOptions.setEnabled(true);
        fogOptions.setColor(new float[]{ 0.8f, 0.8f, 0.8f });
        fogOptions.setDistance(0.0f);
        fogOptions.setCutOffDistance(60.0f);
        fogOptions.setDensity(0.15f);
        fogOptions.setHeight(100.0f);
        fogOptions.setHeightFalloff(0.1f);
    }

    @Override
    public void onFilamentInitialization() {
        ((FilamentPanel) getScene().getPanel()).getView().setFogOptions(fogOptions);
    }

    @Override
    public void onRemoveComponent() {
        ((FilamentPanel) getScene().getPanel()).getView().setFogOptions(new View.FogOptions());
    }

    @SaveState(name = "enabled")
    public boolean getEnabled() {
        return fogOptions.getEnabled();
    }

    @FromXML
    public void setEnabled(boolean enabled) {
        fogOptions.setEnabled(enabled);
    }

    @SaveState(name = "color")
    public ColorObject getColor() {
        return new ColorObject(String.format("rgb(%s,%s,%s)",
                this.fogOptions.getColor()[0] * 255,
                this.fogOptions.getColor()[1] * 255,
                this.fogOptions.getColor()[2] * 255)
        );
    }

    @FromXML
    public void setColor(ColorObject color) {
        fogOptions.setColor(new float[]{
                color.getColor().getRed() / 255f,
                color.getColor().getGreen() / 255f,
                color.getColor().getBlue() / 255f
        });
    }

    @SaveState(name = "distance")
    public float getDistance() {
        return fogOptions.getDistance();
    }

    @FromXML
    public void setDistance(float distance) {
        fogOptions.setDistance(distance);
    }

    @SaveState(name = "cutOffDistance")
    public float getCutOffDistance() {
        return fogOptions.getCutOffDistance();
    }

    @FromXML
    public void setCutOffDistance(float cutOffDistance) {
        fogOptions.setCutOffDistance(cutOffDistance);
    }

    @SaveState(name = "density")
    public float getDensity() {
        return fogOptions.getDensity();
    }

    @FromXML
    public void setDensity(float density) {
        fogOptions.setDensity(density);
    }

    @SaveState(name = "height")
    public float getHeight() {
        return fogOptions.getHeight();
    }

    @FromXML
    public void setHeight(float height) {
        fogOptions.setHeight(height);
    }

    @SaveState(name = "heightFalloff")
    public float getHeightFalloff() {
        return fogOptions.getHeightFalloff();
    }

    @FromXML
    public void setHeightFalloff(float heightFalloff) {
        fogOptions.setHeightFalloff(heightFalloff);
    }

}
