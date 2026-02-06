package pl.AWTGameEngine.components;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.components.types.ComponentFX;
import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.objects.AudioClip;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.TransformSet;

import javax.sound.sampled.*;
import java.io.IOException;

@ComponentFX
@ComponentGL
public class SoundSource extends ObjectComponent {

    private int loop = 0;
    private Clip clip;
    private AudioClip audioClip;
    private boolean running = false;
    private float spreadDistance = 100;

    public SoundSource(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        try {
            clip = AudioSystem.getClip();
            start();
        } catch (LineUnavailableException e) {
            Logger.exception("Cannot get AudioSystem clip", e);
        }
    }

    @Override
    public void onRemoveComponent() {
        clip.stop();
    }

    @Override
    public void onUpdate() {
        if(!running) {
            return;
        }

        TransformSet cameraPosition = new TransformSet(getCamera().getX(), getCamera().getY(), getCamera().getZ());
        FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);



        float newVolume = 1 - ((float) getObject().getPosition().distanceTo(cameraPosition) / spreadDistance);
        if(newVolume < -80) {
            newVolume = -80;
        }
        volume.setValue(newVolume);
    }

    public void start() {
        try {
            clip.open(audioClip.getAudioStream());
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            running = true;
            new Thread(() -> clip.start(), "SoundSource-" + hashCode()).start();
        } catch (LineUnavailableException | IOException e) {
            Logger.exception("Cannot start clip", e);
            running = false;
        }
    }

    public AudioClip getAudioClip() {
        return this.audioClip;
    }

    public Clip getAudioSystemClip() {
        return this.clip;
    }

    @SaveState(name = "clipSource")
    public String getClipSource() {
        return audioClip.getAudioClipPath();
    }

    public void setAudioClip(AudioClip audioClip) {
        this.audioClip = audioClip;
    }

    @FromXML
    public void setClipSource(String source) {
        setAudioClip(Dependencies.getResourceManager().getResourceAsAudioClip(source));
    }

}
