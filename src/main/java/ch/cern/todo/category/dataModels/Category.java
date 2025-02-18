package ch.cern.todo.category.dataModels;

import ch.cern.todo.tasks.dataModels.Task;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(unique = true)
    private String name;
    private String description;
    @OneToMany
    private List<Task> tasks;
    // Attributes to keep track of the milestones
    private Timestamp processedFrom;
    private Timestamp processedTo;

    protected Category(){};

    public CategoryResource transferToResource(){
        return CategoryResource.from(this);
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

    public List<Task> getTasks() {
        return tasks;
    }

    public Timestamp getProcessedFrom() {
        return processedFrom;
    }

    public Timestamp getProcessedTo() {
        return processedTo;
    }
}
