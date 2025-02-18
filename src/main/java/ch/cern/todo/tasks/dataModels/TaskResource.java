package ch.cern.todo.tasks.dataModels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;

import java.sql.Timestamp;

public class TaskResource {
    private String id;
    private String name;
    private String description;
    private Timestamp deadLine;
    private TaskStatus status;
    private TaskPriorityStatus priorityStatus;
    private String assignedTo;
    private String assignedToName;
    private String reportedBy;
    private String reportedByName;
    private String categoryName;


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

    public static TaskResource from(Task task){
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

    public static TaskResource from(Task task, String assignedToName, String reportedByName){
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

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Timestamp getDeadLine() {
        return deadLine;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskPriorityStatus getPriorityStatus() {
        return priorityStatus;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public String getAssignedToName() {
        return assignedToName;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public String getReportedByName() {
        return reportedByName;
    }
}
