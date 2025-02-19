package ch.cern.todo.searchEngine;
import ch.cern.todo.exceptions.InvalidSearchCriteriaException;
import ch.cern.todo.tasks.dataModels.Task;
import jakarta.persistence.criteria.CriteriaQuery;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.Serializable;

public class TaskSearchEngineService implements Specification<Task>, Serializable {

    private final SearchCriteria criteria;

    public TaskSearchEngineService(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Path expression;
        try {
            if (criteria.getKey().contains(".")) {
                String[] parts = criteria.getKey().split("\\.");
                Join<Object, Object> join = root.join(parts[0], JoinType.LEFT);
                expression = join.get(parts[1]);
            } else {
                expression = root.get(criteria.getKey());
            }
        } catch (IllegalArgumentException ex) {
            throw new InvalidSearchCriteriaException("Invalid search field: " + criteria.getKey());
        }

        Class attributeType = expression.getJavaType();
        Object convertedValue = SearchEngineHelper.convertValue(criteria.getValue(), attributeType);

        return SearchEngineHelper.getPredicate(builder, expression, convertedValue, criteria.getOperation());
    }
    }
