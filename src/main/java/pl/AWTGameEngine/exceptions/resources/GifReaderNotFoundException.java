package pl.AWTGameEngine.exceptions.resources;

public class GifReaderNotFoundException extends RuntimeException {

    public GifReaderNotFoundException() {
        super("GIF format reader not found.");
    }

}
