package ch.cern.todo.tasks.dataModels;

import ch.cern.todo.category.dataModels.Category;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;

import java.sql.Timestamp;


public record TaskResource(String id, String name, String description, Timestamp deadLine, TaskStatus status,
                           TaskPriorityStatus priorityStatus, String assignedTo, String assignedToName,
                           String reportedBy, String reportedByName, String categoryName) {
    @JsonCreator
    public TaskResource(@JsonProperty String id,
                        @JsonProperty @NonNull String name,
                        @JsonProperty String description,
                        @JsonProperty @NonNull Timestamp deadLine,
                        @JsonProperty @NonNull TaskStatus status,
                        @JsonProperty @NonNull TaskPriorityStatus priorityStatus,
                        @JsonProperty String assignedTo,
                        @JsonProperty String assignedToName,
                        @JsonProperty @NonNull String reportedBy,
                        @JsonProperty String reportedByName,
                        @JsonProperty @NonNull String categoryName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.deadLine = deadLine;
        this.status = status;
        this.priorityStatus = priorityStatus;
        this.assignedTo = assignedTo;
        this.assignedToName = assignedToName;
        this.reportedBy = reportedBy;
        this.reportedByName = reportedByName;
        this.categoryName = categoryName;
    }

    public static TaskResource from(Task task) {
        return new TaskResource(
                task.getId(),
                task.getName(),
                null,
                task.getDeadLine(),
                task.getStatus(),
                task.getPriorityStatus(),
                null,
                null,
                task.getReportedBy(),
                null,
                task.getCategory().getName());
    }

    public static TaskResource from(Task task, String assignedToName, String reportedByName) {
        return new TaskResource(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getDeadLine(),
                task.getStatus(),
                task.getPriorityStatus(),
                task.getAssignedTo(),
                assignedToName,
                task.getReportedBy(),
                reportedByName,
                task.getCategory().getName());
    }

    public Task transferToEntity(TaskStatus taskStatus, Category category) {
        return Task.from(this, taskStatus, category);
    }

    public Task transferToEntity(Task task) {
        return Task.from(this, task);
    }
}
