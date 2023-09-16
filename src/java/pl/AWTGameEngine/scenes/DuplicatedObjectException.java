package pl.AWTGameEngine.scenes;

public class DuplicatedObjectException extends Exception {

    public DuplicatedObjectException(String identifier) {
        super("Object with name " + identifier + " already exists in this scene.");
    }

}
