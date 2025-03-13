package nova;

/**
 * A custom exception class for the Nova application.
 */
public class NovaException extends Exception {
    public NovaException(String message) {
        super(message);
    }
}