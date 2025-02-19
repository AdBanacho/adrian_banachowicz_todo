package ch.cern.todo.searchEngine;

import ch.cern.todo.exceptions.InvalidSearchCriteriaException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public class SearchEngineHelper {

    public static Predicate getPredicate(CriteriaBuilder builder, Path expression, Object convertedValue, String operation) {
        switch (operation.toUpperCase()) {
            case "=":
                return builder.equal(expression, convertedValue);
            case "!=":
                return builder.notEqual(expression, convertedValue);
            case ">":
                return builder.greaterThan(expression, (Comparable) convertedValue);
            case ">=":
                return builder.greaterThanOrEqualTo(expression, (Comparable) convertedValue);
            case "<":
                return builder.lessThan(expression, (Comparable) convertedValue);
            case "<=":
                return builder.lessThanOrEqualTo(expression, (Comparable) convertedValue);
            case ":":
            case "LIKE":
                return builder.like(builder.lower(expression),
                        "%" + convertedValue.toString().toLowerCase() + "%");
            default:
                throw new UnsupportedOperationException("Operation " + operation + " is not supported");

        }
    }

    public static Object convertValue(Object value, Class targetType) {
        if (targetType.isInstance(value)) {
            return value;
        }
        try {
            if (targetType.isEnum()) {
                return Enum.valueOf(targetType, value.toString());
            } else if (targetType.equals(Integer.class)) {
                return Integer.valueOf(value.toString());
            } else if (targetType.equals(Long.class)) {
                return Long.valueOf(value.toString());
            } else if (targetType.equals(Double.class)) {
                return Double.valueOf(value.toString());
            } else if (targetType.equals(Float.class)) {
                return Float.valueOf(value.toString());
            } else if (targetType.equals(java.sql.Timestamp.class)) {
                return java.sql.Timestamp.valueOf(value.toString().replace("T", " "));
            } else if (targetType.equals(String.class)) {
                return value.toString();
            }
        } catch (Exception e) {
            throw new InvalidSearchCriteriaException("Failed to convert value: " + value
                    + " to type " + targetType.getSimpleName());
        }
        return value;
    }
}
