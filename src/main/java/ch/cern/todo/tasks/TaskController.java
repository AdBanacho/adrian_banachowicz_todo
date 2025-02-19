package ch.cern.todo.tasks;

import ch.cern.todo.searchEngine.SearchCriteria;
import ch.cern.todo.tasks.dataModels.TaskResource;
import ch.cern.todo.tasks.dataModels.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    /**
     * Retrieves a paginated list of tasks based on search criteria.
     * <p>
     * Accessible by users with roles "USER" or "ADMIN".
     * </p>
     *
     * @param page               the page number to retrieve (default is 0).
     * @param size               the number of items per page (default is 10).
     * @param sortBy             the field used for sorting (default is "id").
     * @param ascending          whether to sort in ascending order (default is true).
     * @param searchCriteriaList the list of search criteria used to filter tasks.
     * @return a ResponseEntity containing a Page of TaskResource objects.
     */
    @GetMapping(value = "")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<TaskResource>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending,
            @RequestBody List<SearchCriteria> searchCriteriaList) {

        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TaskResource> allTasks = taskService.getAllTasks(pageable, searchCriteriaList);
        return ResponseEntity.ok(allTasks);
    }

    /**
     * Retrieves a single task by its unique identifier.
     * <p>
     * Accessible by users with roles "USER" or "ADMIN".
     * </p>
     *
     * @param id the unique identifier of the task.
     * @return a ResponseEntity containing the TaskResource representing the task.
     */
    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TaskResource> getTask(@PathVariable String id){
        return ResponseEntity.ok(taskService.getTask(id));
    }

    /**
     * Creates a new task.
     * <p>
     * Accessible by users with roles "USER" or "ADMIN".
     * </p>
     *
     * @param taskResource the TaskResource containing task details.
     * @return a ResponseEntity containing the saved TaskResource.
     */
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TaskResource> saveTask(@RequestBody TaskResource taskResource){
        return ResponseEntity.ok(taskService.saveTask(taskResource));
    }

    /**
     * Updates the details of an existing task.
     * <p>
     * Accessible by users with roles "USER" or "ADMIN".
     * </p>
     *
     * @param taskResource the TaskResource containing updated task details.
     * @return a ResponseEntity containing the updated TaskResource.
     */
    @PutMapping(value = "/updateDetails")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TaskResource> updateDetails(@RequestBody TaskResource taskResource){
        return ResponseEntity.ok(taskService.updateDetails(taskResource));
    }

    /**
     * Updates the status of a task.
     * <p>
     * Accessible by users with roles "USER" or "ADMIN".
     * </p>
     *
     * @param id         the unique identifier of the task.
     * @param taskStatus the new status to be set.
     * @return a ResponseEntity containing the updated TaskResource.
     */
    @PutMapping(value = "/updateStatus/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TaskResource> updateStatus(@PathVariable String id,
                                                     @RequestParam TaskStatus taskStatus){
        return ResponseEntity.ok(taskService.updateStatus(id, taskStatus));
    }

    /**
     * Updates the category associated with a task.
     * <p>
     * Accessible only by users with the "ADMIN" role.
     * </p>
     *
     * @param id           the unique identifier of the task.
     * @param categoryName the new category name to be associated with the task.
     * @return a ResponseEntity containing the updated TaskResource.
     */
    @PutMapping(value = "/updateCategory/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskResource> updateCategory(@PathVariable String id,
                                                       @RequestParam String categoryName){
        return ResponseEntity.ok(taskService.updateCategory(id, categoryName));
    }

    /**
     * Deletes a task by its unique identifier.
     * <p>
     * Accessible only by users with the "ADMIN" role.
     * </p>
     *
     * @param id the unique identifier of the task to be deleted.
     * @return a ResponseEntity with an OK status if deletion is successful.
     */
    @PutMapping(value = "/deleteTask/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }
}
