package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Sprite;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Unique
public class Animator extends ObjectComponent {

    private final List<AnimationFrame> frames = new ArrayList<>();
    private final List<Integer> delays = new ArrayList<>();
    private boolean loop = true;
    private int iterator = 0;
    private int timer = 0;

    public Animator(GameObject object) {
        super(object);
    }

    @Override
    public void onUpdate() {
        if(iterator > frames.size() - 1) {
            if(loop) {
                iterator = 0;
            }
        }
        int delay = 0;
        if(iterator < delays.size()) {
            delay = delays.get(iterator);
        }
        if(timer > delay) {
            timer = 0;
        } else {
            timer++;
            return;
        }
        for(ObjectComponent component : getObject().getComponentsByClass(SpriteRenderer.class)) {
            SpriteRenderer spriteRenderer = (SpriteRenderer) component;
            spriteRenderer.setSprite(frames.get(iterator).getSprite());
            iterator++;
        }
    }

    @SerializationGetter
    public boolean isLoop() {
        return this.loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    @SerializationSetter
    public void setLoop(String loop) {
        this.loop = Boolean.parseBoolean(loop);
    }

    public void setNextFrame(AnimationFrame frame) {
        frames.add(frame);
    }

    @SerializationSetter
    public void setNextFrame(String source) {
        AnimationFrame animFrame = new AnimationFrame();
        animFrame.setSprite(source);
        setNextFrame(animFrame);
    }

    public void setNextFrame(Sprite sprite) {
        AnimationFrame animFrame = new AnimationFrame();
        animFrame.setSprite(sprite);
        setNextFrame(animFrame);
    }

    public void setNextDelay(int delay) {
        delays.add(delay);
    }

    @SerializationSetter
    public void setNextDelay(String delay) {
        try {
            setNextDelay(Integer.parseInt(delay));
        } catch(NumberFormatException e) {
            setNextDelay(0);
        }
    }

    public static class AnimationFrame {

        private Sprite sprite;

        public Sprite getSprite() {
            return this.sprite;
        }

        public void setSprite(Sprite sprite) {
            this.sprite = sprite;
        }

        public void setSprite(String spriteSource) {
            setSprite(ResourceManager.getResourceAsSprite(spriteSource));
        }

    }

}