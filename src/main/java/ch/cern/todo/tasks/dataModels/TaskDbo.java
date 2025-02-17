package ch.cern.todo.tasks.dataModels;

import ch.cern.todo.category.dataModels.CategoryDbo;
import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.sql.Timestamp;

@Entity
public class TaskDbo{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String description;
    private Timestamp deadLine;
    private TaskStatus status;
    @ManyToOne
    private CategoryDbo category;
    // Attributes to keep track of the milestones
    private Timestamp processedFrom;
    private Timestamp processedTo;

    protected TaskDbo(){}

    public TaskDbo(@NonNull String name,
                   String description,
                   Timestamp deadLine,
                   TaskStatus status,
                   CategoryDbo category,
                   Timestamp processedFrom,
                   Timestamp processedTo) {
        this.name = name;
        this.description = description;
        this.deadLine = deadLine;
        this.status = status;
        this.category = category;
        this.processedFrom = processedFrom;
        this.processedTo = processedTo;
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

    public CategoryDbo getCategory() {
        return category;
    }

    public Timestamp getProcessedFrom() {
        return processedFrom;
    }

    public Timestamp getProcessedTo() {
        return processedTo;
    }
}
