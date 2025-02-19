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

    @GetMapping(value="")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<TaskResource>> getAllTasks(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestParam(defaultValue = "id") String sortBy,
                                                          @RequestParam(defaultValue = "true") boolean ascending,
                                                          @RequestBody List<SearchCriteria> searchCriteriaList){
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TaskResource> allTasks = taskService.getAllTasks(pageable, searchCriteriaList);
        return ResponseEntity.ok(allTasks);
    }

    @GetMapping(value="/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TaskResource> getTask(@PathVariable String id){
        return ResponseEntity.ok(taskService.getTask(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TaskResource> saveTask(@RequestBody TaskResource taskResource){
        return ResponseEntity.ok(taskService.saveTask(taskResource));
    }

    @PutMapping(value = "/updateDetails")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TaskResource> updateDetails(@RequestBody TaskResource taskResource){
        return ResponseEntity.ok(taskService.updateDetails(taskResource));
    }

    @PutMapping(value = "/updateStatus/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TaskResource> updateStatus(@PathVariable String id, @RequestParam TaskStatus taskStatus){
        return ResponseEntity.ok(taskService.updateStatus(id, taskStatus));
    }

    @PutMapping(value = "/updateCategory/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskResource> updateCategory(@PathVariable String id, @RequestParam String categoryName){
        return ResponseEntity.ok(taskService.updateCategory(id, categoryName));
    }

    @PutMapping(value = "/deleteTask/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }
}
