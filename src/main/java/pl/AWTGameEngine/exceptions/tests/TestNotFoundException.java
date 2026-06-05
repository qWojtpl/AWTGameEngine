package pl.AWTGameEngine.exceptions.tests;

public class TestNotFoundException extends RuntimeException {

    public TestNotFoundException(String name) {
        super("Test " + name + " not found.");
    }

}
