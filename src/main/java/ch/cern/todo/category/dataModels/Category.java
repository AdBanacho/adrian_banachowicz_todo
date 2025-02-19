package ch.cern.todo.category.dataModels;

import ch.cern.todo.tasks.dataModels.Task;
import ch.cern.todo.tasks.dataModels.TaskResource;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@IdClass(CategoryKey.class)
public class Category {
    @Id
    private String id;
    private String name;
    private String description;
    private CategoryStatus status;
    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
    private List<Task> tasks;
    // Attributes to keep track of the milestones
    private Timestamp processedFrom;
    @Id
    @Column(name = "processed_to")
    private Timestamp processedTo;

    protected Category(){};

    public Category(String id,
                    String name,
                    String description,
                    CategoryStatus status,
                    List<Task> tasks,
                    Timestamp processedFrom,
                    Timestamp processedTo){
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.tasks = tasks;
        this.processedFrom = processedFrom;
        this.processedTo = processedTo;
    }

    public static Category from(CategoryResource categoryResource){
        return new Category(
                UUID.randomUUID().toString(),
                categoryResource.name(),
                categoryResource.description(),
                CategoryStatus.ACTIVE,
                Collections.emptyList(),
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.of(9999,12,31,12,0,0))
        );
    }

    public static Category from(CategoryResource categoryResource, Category category){
        return new Category(
                category.getId(),
                categoryResource.name(),
                categoryResource.description(),
                category.getStatus(),
                category.getTasks(),
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.of(9999,12,31,12,0,0))
        );
    }
    public Category updateStatus(CategoryStatus categoryStatus){
        return new Category(
                getId(),
                getName(),
                getDescription(),
                categoryStatus,
                categoryStatus.isDeleted() ? Collections.emptyList() : getTasks(),
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

    public CategoryStatus getStatus() {
        return status;
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

    public void closeCategoryEntity() {
        this.processedTo = Timestamp.valueOf(LocalDateTime.now());
        this.tasks = Collections.emptyList();
    }
}
