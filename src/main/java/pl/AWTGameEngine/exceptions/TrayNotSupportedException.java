package pl.AWTGameEngine.exceptions;

public class TrayNotSupportedException extends RuntimeException {

    public TrayNotSupportedException() {
        super("Cannot display Tray, because trays aren't supported in this system!");
    }

}
