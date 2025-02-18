package ch.cern.todo.category.dataModels;

import ch.cern.todo.tasks.dataModels.Task;
import ch.cern.todo.tasks.dataModels.TaskResource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;

import java.util.List;

public record CategoryResource(String id, String name, String description, List<TaskResource> tasks) {
    @JsonCreator
    public CategoryResource(@JsonProperty String id,
                            @JsonProperty @NonNull String name,
                            @JsonProperty String description,
                            @JsonProperty List<TaskResource> tasks) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tasks = tasks;
    }

    public static CategoryResource from(Category category) {
        return new CategoryResource(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getTasks().stream().map(Task::transferToResource).toList()
        );
    }

    public static CategoryResource from(Category category, List<TaskResource> tasksWithDetails) {
        return new CategoryResource(
                category.getId(),
                category.getName(),
                category.getDescription(),
                tasksWithDetails
        );
    }

    public Category transferToNewEntity(){
        return Category.from(this);
    }
    public Category transferToExistingEntity(Category category){
        return Category.from(this, category);
    }

}

