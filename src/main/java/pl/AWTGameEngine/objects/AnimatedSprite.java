package pl.AWTGameEngine.objects;

import pl.AWTGameEngine.annotations.Command;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * AnimatedSprite is an instance of Sprite.
 * If you're using <code>getImage</code> instead of <code>requestSprite</code>,
 * first frame of an AnimatedSprite will be returned.
 */
public class AnimatedSprite extends Sprite {

    private final List<Sprite> sprites;
    private long lastFetch = System.currentTimeMillis();
    int counter = 0;

    public AnimatedSprite(String imagePath, List<Sprite> sprites) {
        super(imagePath, null);
        this.sprites = sprites;
    }

    /**
     * Request frame of animated sprite.
     * @return Current animated sprite frame
     */
    public Sprite requestSprite() {
        if(counter >= sprites.size()) {
            counter = 0;
        }
        Sprite sprite = sprites.get(counter);
        if(System.currentTimeMillis() - lastFetch > 100) {
            counter++;
            lastFetch = System.currentTimeMillis();
        }
        return sprite;
    }

    @Override
    public BufferedImage getImage() {
        return sprites.get(0).getImage();
    }

    public String getImagePath() {
        return super.getImagePath();
    }

    @Override
    public void updateBufferedImage(BufferedImage bufferedImage) {

    }

    public Sprite toNegative() {
        for(Sprite sprite : sprites) {
            sprite.toNegative();
        }
        return this;
    }

    public Sprite scale(double scale) {
        for(Sprite sprite : sprites) {
            sprite.scale(scale);
        }
        return this;
    }

    @Command(value = "base64")
    public String getImageBase64() {
        return getImageBase64(false);
    }

    //TODO
    public String getImageBase64(boolean cacheResult) {
        return null;
    }

    @Override
    public String toString() {
        return super.getImagePath();
    }

}
