package exception;

/**
 * Exception thrown when attempting to add a duplicate element to a class
 */
public class DuplicateElementException extends Exception {
    public DuplicateElementException(String message) {
        super(message);
    }
} 