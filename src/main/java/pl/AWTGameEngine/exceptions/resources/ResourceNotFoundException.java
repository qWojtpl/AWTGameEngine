package pl.AWTGameEngine.exceptions.resources;

public class ResourceNotFoundException extends SecurityException {

    public ResourceNotFoundException() {
        super("Resource not found.");
    }

}