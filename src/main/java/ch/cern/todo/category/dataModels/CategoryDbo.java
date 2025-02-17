package ch.cern.todo.category.dataModels;

import ch.cern.todo.tasks.dataModels.TaskDbo;
import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.sql.Timestamp;
import java.util.List;

@Entity
public class CategoryDbo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(unique = true)
    private String name;
    private String description;
    @OneToMany
    private List<TaskDbo> tasks;
    // Attributes to keep track of the milestones
    private Timestamp processedFrom;
    private Timestamp processedTo;

    protected CategoryDbo(){};

    public CategoryDbo(@NonNull String name,
                       String description,
                       List<TaskDbo> tasks,
                       Timestamp processedFrom,
                       Timestamp processedTo) {
        this.name = name;
        this.description = description;
        this.tasks = tasks;
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

    public List<TaskDbo> getTasks() {
        return tasks;
    }

    public Timestamp getProcessedFrom() {
        return processedFrom;
    }

    public Timestamp getProcessedTo() {
        return processedTo;
    }
}
