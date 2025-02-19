package ch.cern.todo.validation;

import ch.cern.todo.category.dataModels.Category;
import ch.cern.todo.exceptions.EntityAlreadyExistsException;
import ch.cern.todo.exceptions.EntityNotExistException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class InputFieldValidator {

    private InputFieldValidator() {
    }

    public static void validateIfEntityExists(String entityName, String fieldName, String id) {
        if (id != null) {
            throw new EntityAlreadyExistsException((entityName + " with " + fieldName + ": " + id + " already exists"));
        }
    }

    public static <T> void validateIfNotEntityExists(String entityName, String fieldName, T object) {
        if (object == null) {
            throw new EntityNotExistException((entityName + " \"" + fieldName + "\" not exists"));
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

    public static <T> void validateIfFieldChanged(T fieldOfEntity, T fieldOfResource, List<String> errorMessages) {
        if (!Objects.equals(fieldOfEntity, fieldOfResource)) {
            errorMessages.add(fieldOfEntity + " is not equal to " + fieldOfResource + ". Update not allowed.");
        }
    }

    public static void validateIfCategoryNameIsUnique(Category existingCategorySameId, Category existingCategorySameName, List<String> errorMessages){
        String sameIdId = Optional.ofNullable(existingCategorySameId).map(Category::getId).orElse(null);
        String sameNameId = Optional.ofNullable(existingCategorySameName).map(Category::getId).orElse(null);
        if (sameIdId != null && sameNameId != null && !sameIdId.equals(sameNameId)){
            errorMessages.add("Name " + existingCategorySameName.getName() +  " is used by category of id: " + sameNameId);
        }
    }
}
