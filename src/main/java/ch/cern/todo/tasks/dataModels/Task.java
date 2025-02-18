package ch.cern.todo.tasks.dataModels;

import ch.cern.todo.category.dataModels.Category;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
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
    private Category category;
    // Attributes to keep track of the milestones
    private Timestamp processedFrom;
    private Timestamp processedTo;

    protected Task(){}

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

    public Timestamp getProcessedFrom() {
        return processedFrom;
    }

    public Timestamp getProcessedTo() {
        return processedTo;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public String getReportedBy() {
        return reportedBy;
    }
}
