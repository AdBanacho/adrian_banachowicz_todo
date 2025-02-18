package ch.cern.todo.category.dataModels;

import ch.cern.todo.tasks.dataModels.Task;
import ch.cern.todo.tasks.dataModels.TaskResource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CategoryResource {
    private String id;
    private String name;
    private String description;
    private List<TaskResource> tasks;

    @JsonCreator
    public CategoryResource(@JsonProperty String id,
                            @JsonProperty String name,
                            @JsonProperty String description,
                            @JsonProperty List<TaskResource> tasks) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tasks = tasks;
    }

    public static CategoryResource from(Category category){
        return new CategoryResource(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getTasks().stream().map(Task::transferToResource).toList()
        );
    }

    public static CategoryResource from(Category category, List<TaskResource> tasksWithDetails){
        return new CategoryResource(
                category.getId(),
                category.getName(),
                category.getDescription(),
                tasksWithDetails
        );
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<TaskResource> getTasks() {
        return tasks;
    }
}

