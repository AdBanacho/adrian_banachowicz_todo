package ch.cern.todo.exceptions;

public class InvalidSearchCriteriaException extends RuntimeException {
    public InvalidSearchCriteriaException(String message) {
        super(message);
    }
}
