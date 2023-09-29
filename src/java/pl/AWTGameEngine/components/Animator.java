package pl.AWTGameEngine.components;

import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Animator extends ObjectComponent {

    private final List<AnimationFrame> frames = new ArrayList<>();
    private final List<Integer> delays = new ArrayList<>();
    private boolean loop = true;
    private int iterator = 0;
    private int timer = 0;

    public Animator(GameObject object) {
        super(object);
        setUnique(true);
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
            spriteRenderer.setImage(frames.get(iterator).getImage());
            iterator++;
        }
    }

    public boolean isLoop() {
        return this.loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void setLoop(String loop) {
        this.loop = Boolean.parseBoolean(loop);
    }

    public void setNextFrame(AnimationFrame frame) {
        frames.add(frame);
    }

    public void setNextFrame(String source) {
        AnimationFrame animFrame = new AnimationFrame();
        animFrame.setImage(source);
        setNextFrame(animFrame);
    }

    public void setNextFrame(Image image) {
        AnimationFrame animFrame = new AnimationFrame();
        animFrame.setImage(image);
        setNextFrame(animFrame);
    }

    public void setNextDelay(int delay) {
        delays.add(delay);
    }

    public void setNextDelay(String delay) {
        try {
            setNextDelay(Integer.parseInt(delay));
        } catch(NumberFormatException e) {
            setNextDelay(0);
        }
    }

    public static class AnimationFrame {

        private Image image;

        public Image getImage() {
            return this.image;
        }

        public void setImage(Image image) {
            this.image = image;
        }

        public void setImage(String imageSource) {
            setImage(ResourceManager.getResourceAsImage(imageSource));
        }

    }

}