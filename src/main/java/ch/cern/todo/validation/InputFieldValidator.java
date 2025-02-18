package ch.cern.todo.validation;

import ch.cern.todo.exceptions.EntityAlreadyExistsException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public final class InputFieldValidator {

    private InputFieldValidator() {
    }

    public static void validateIfEntityExists(String entityName, String fieldName, String id) {
        if (id != null) {
            throw new EntityAlreadyExistsException((entityName + " with " + fieldName + ": " + id + " already exists"));
        }
    }

    public static void validateFieldNotEmpty(String entityName, String fieldName, String fieldValue, List<String> errorMessages) {
        if (fieldValue == null || fieldValue.trim().isEmpty()) {
            errorMessages.add(entityName + " has empty field '" + fieldName + "'");
        }
    }

    public static void validateTimeIfNotInPast(String fieldName, Timestamp fieldValue, List<String> errorMessages) {
        if (fieldValue.before(Timestamp.valueOf(LocalDateTime.now()))) {
            errorMessages.add(fieldName + " cannot be in the past");
        }
    }

}
