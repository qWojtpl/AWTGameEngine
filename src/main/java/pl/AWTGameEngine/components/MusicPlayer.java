package pl.AWTGameEngine.components;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.DefaultComponent;
import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.FromXML;
import pl.AWTGameEngine.annotations.WebComponent;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.objects.AudioClip;
import pl.AWTGameEngine.objects.GameObject;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

@DefaultComponent
@WebComponent
public class MusicPlayer extends ObjectComponent {

    private Clip clip;
    private AudioClip audioClip;
    private boolean autoPlay = true;
    private int loopCount = -1;

    public MusicPlayer(GameObject object) {
        super(object);
        try {
            clip = AudioSystem.getClip();
        } catch(LineUnavailableException e) {
            Logger.exception("Line is unavailable", e);
            clip = null;
        }
    }

    @Override
    public void onRemoveComponent() {
        stop();
    }

    private void logClip() {
        Logger.error("Cannot open clip. Clip cannot be initialized.");
    }

    private void open() {
        if(audioClip == null) {
            Logger.error("Cannot open clip. AudioClip is not set.");
            return;
        }
        if(clip == null) {
            logClip();
            return;
        }
        try {
            AudioInputStream stream = audioClip.getAudioStream();
            if(stream != null) {
                clip.open(stream);
            }
            if(autoPlay) {
                play();
            }
        } catch(LineUnavailableException | IOException e) {
            Logger.exception("Line is unavailable", e);
        }
    }

    public void play() {
        if(clip == null) {
            logClip();
            return;
        }
        clip.setFramePosition(0);
        setLoopCount(loopCount);
        (new Thread(clip::start)).start();
    }

    public void stop() {
        if(clip == null) {
            logClip();
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

    @SerializationGetter
    public boolean isAutoPlay() {
        return this.autoPlay;
    }

    @SerializationGetter
    public int getLoopCount() {
        return this.loopCount;
    }

    public void setSource(AudioClip audioClip) {
        this.audioClip = audioClip;
        if(clip != null) {
            open();
        }
    }

    @FromXML
    public void setSourcePath(String sourcePath) {
        setSource(Dependencies.getResourceManager().getResourceAsAudioClip(sourcePath));
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    @FromXML
    public void setAutoPlay(String autoPlay) {
        setAutoPlay(Boolean.parseBoolean(autoPlay));
    }

    public void setLoopCount(int loop) {
        this.loopCount = loop;
        if(loop == -1) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            clip.loop(this.loopCount);
        }
    }

    @FromXML
    public void setLoopCount(String loop) {
        setLoopCount(Integer.parseInt(loop));
    }

}
