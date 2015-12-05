package nl.tudelft.planningstool.api.security;

public class NotAUserException extends Exception {
    public NotAUserException(String msg) {
        super(msg);
    }
}
