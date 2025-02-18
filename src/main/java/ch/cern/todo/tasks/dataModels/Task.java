package ch.cern.todo.tasks.dataModels;

import ch.cern.todo.category.dataModels.Category;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@IdClass(TaskKey.class)
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String description;
    private Timestamp deadLine;
    private TaskStatus status;
    private TaskPriorityStatus priorityStatus;
    private String assignedTo;
    private String reportedBy;
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "category_id", referencedColumnName = "id"),
            @JoinColumn(name = "category_processed_to", referencedColumnName = "processed_to")
    })
    private Category category;
    // Attributes to keep track of the milestones
    private Timestamp processedFrom;
    @Id
    private Timestamp processedTo;

    protected Task(){}

    public Task(String id,
                String name,
                String description,
                Timestamp deadLine,
                TaskStatus status,
                TaskPriorityStatus priorityStatus,
                String assignedTo,
                String reportedBy,
                Category category,
                Timestamp processedFrom,
                Timestamp processedTo
                ){
        this.id = id;
        this.name = name;
        this.description = description;
        this.deadLine = deadLine;
        this.status = status;
        this.priorityStatus = priorityStatus;
        this.assignedTo = assignedTo;
        this.reportedBy = reportedBy;
        this.category = category;
        this.processedFrom = processedFrom;
        this.processedTo = processedTo;
    }

    public static Task from(TaskResource taskResource, TaskStatus taskStatus, Category category){
        return new Task(
                null,
                taskResource.name(),
                taskResource.description(),
                taskResource.deadLine(),
                taskStatus,
                taskResource.priorityStatus(),
                taskResource.assignedTo(),
                taskResource.reportedBy(),
                category,
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.of(9999,12,31,12,0,0))
        );
    }

    public static Task from(TaskResource taskResource, Task task){
        return new Task(
                task.id,
                taskResource.name(),
                taskResource.description(),
                taskResource.deadLine(),
                task.getStatus(),
                taskResource.priorityStatus(),
                taskResource.assignedTo(),
                task.getReportedBy(),
                task.getCategory(),
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.of(9999,12,31,12,0,0))
        );
    }

    public TaskResource transferToResource(){
        return TaskResource.from(this);
    }

    public TaskResource transferToResource(String assignedToName, String reportedByName){
        return TaskResource.from(this, assignedToName, reportedByName);
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

    public Category getCategory() {
        return category;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public Timestamp getProcessedFrom() {
        return processedFrom;
    }

    public Timestamp getProcessedTo() {
        return processedTo;
    }

    public void closeTaskEntity() {
        this.processedTo = Timestamp.valueOf(LocalDateTime.now());
        this.category = null;
    }


}
