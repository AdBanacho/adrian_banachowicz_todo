package ch.cern.todo.category.dataModels;

import ch.cern.todo.tasks.dataModels.Task;
import ch.cern.todo.tasks.dataModels.TaskResource;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Entity
@IdClass(CategoryKey.class)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(unique = true)
    private String name;
    private String description;
    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
    private List<Task> tasks;
    // Attributes to keep track of the milestones
    private Timestamp processedFrom;
    @Id
    @Column(name = "processed_to")
    private Timestamp processedTo;

    protected Category(){};

    public Category(String name,
                    String description,
                    List<Task> tasks,
                    Timestamp processedFrom,
                    Timestamp processedTo){
        this.id = null;
        this.name = name;
        this.description = description;
        this.tasks = tasks;
        this.processedFrom = processedFrom;
        this.processedTo = processedTo;
    }

    public static Category from(CategoryResource categoryResource){
        return new Category(
                categoryResource.name(),
                categoryResource.description(),
                Collections.emptyList(),
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.of(9999,12,31,12,0,0))
        );
    }

    public CategoryResource transferToResource(){
        return CategoryResource.from(this);
    }

    public CategoryResource transferToResource(List<TaskResource> tasksWithDetails){
        return CategoryResource.from(this, tasksWithDetails);
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
