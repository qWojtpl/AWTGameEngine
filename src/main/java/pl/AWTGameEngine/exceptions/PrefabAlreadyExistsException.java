package pl.AWTGameEngine.exceptions;

public class PrefabAlreadyExistsException extends RuntimeException {

    public PrefabAlreadyExistsException(String identifier) {
        super("Prefab with identifier " + identifier + " already exists.");
    }

}
