package pl.AWTGameEngine.objects;

import javax.sound.sampled.AudioInputStream;

public class AudioClip {

    private final String audioClipPath;
    private final AudioInputStream audioStream;

    public AudioClip(String audioClipPath, AudioInputStream audioStream) {
        this.audioClipPath = audioClipPath;
        this.audioStream = audioStream;
    }

    public String getAudioClipPath() {
        return this.audioClipPath;
    }

    public AudioInputStream getAudioStream() {
        return this.audioStream;
    }

}
