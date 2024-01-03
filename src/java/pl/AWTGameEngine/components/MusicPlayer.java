package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.objects.AudioClip;
import pl.AWTGameEngine.objects.GameObject;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public class MusicPlayer extends ObjectComponent {

    private Clip clip;
    private AudioClip audioClip;
    private boolean autoPlay = true;

    public MusicPlayer(GameObject object) {
        super(object);
        try {
            clip = AudioSystem.getClip();
        } catch(LineUnavailableException e) {
            Logger.log("Line is unavailable", e.getMessage(), e.getStackTrace());
            clip = null;
            return;
        }
        if(audioClip != null) {
            open();
        }
    }

    @Override
    public void onRemoveComponent() {
        stop();
    }

    private void open() {
        try {
            AudioInputStream stream = audioClip.getAudioStream();
            if(stream != null) {
                clip.open(stream);
            }
            if(autoPlay) {
                play();
            }
        } catch(LineUnavailableException | IOException e) {
            Logger.log("Line is unavailable", e.getMessage(), e.getStackTrace());
        }
    }

    public void play() {
        if(clip == null) {
            return;
        }
        clip.start();
    }

    public void stop() {
        if(clip == null) {
            return;
        }
        clip.stop();
    }

    public AudioClip getSource() {
        return this.audioClip;
    }

    @SerializationGetter
    public String getSourcePath() {
        return this.audioClip.getAudioClipPath();
    }

    public boolean isAutoPlay() {
        return this.autoPlay;
    }

    public void setSource(AudioClip audioClip) {
        this.audioClip = audioClip;
        if(clip != null) {
            open();
        }
    }

    @SerializationSetter
    public void setSourcePath(String sourcePath) {
        setSource(ResourceManager.getResourceAsAudioClip(sourcePath));
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

}
