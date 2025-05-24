package service;

public class AlreadyTakenException extends Exception {
    //figure out some way to associate error # with specific exception
    public AlreadyTakenException(String message) {
        super(message);
    }
    public AlreadyTakenException(String message, Throwable ex) {
        super(message, ex);
    }
}
