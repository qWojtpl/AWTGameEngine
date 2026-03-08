package pl.AWTGameEngine.exceptions;

import java.util.List;

public class JavaBuildException extends RuntimeException {

    public JavaBuildException(List<String> errors) {
        super("Java build ended with errors: " + String.join(", ", errors));
    }

}
