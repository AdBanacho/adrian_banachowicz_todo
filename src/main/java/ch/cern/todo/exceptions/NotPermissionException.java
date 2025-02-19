package ch.cern.todo.exceptions;

public class NotPermissionException extends RuntimeException {
    public NotPermissionException(String message) {
        super(message);
    }
}
