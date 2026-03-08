package pl.AWTGameEngine.exceptions.scenes;

public class SceneOwnershipException extends RuntimeException {

    public SceneOwnershipException() {
        super("Scene must be owned by this window!");
    }

}
