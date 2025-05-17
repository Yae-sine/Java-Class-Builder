package exception;

/**
 * Exception thrown when an invalid name is provided for a class or class element
 */
public class InvalidNameException extends Exception {
    public InvalidNameException(String message) {
        super(message);
    }
} 