package ch.cern.todo.tasks;

import ch.cern.todo.tasks.dataModels.TaskResource;
import ch.cern.todo.tasks.dataModels.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    private TaskController(TaskService taskService){
        this.taskService = taskService;
    };

    @GetMapping(value="")
    public ResponseEntity<Page<TaskResource>> getAllTasks(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestParam(defaultValue = "id") String sortBy,
                                                          @RequestParam(defaultValue = "true") boolean ascending){
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TaskResource> allTasks = taskService.getAllTasks(pageable);
        return ResponseEntity.ok(allTasks);
    }

    @GetMapping(value="/{id}")
    public ResponseEntity<TaskResource> getTask(@PathVariable String id){
        return ResponseEntity.ok(taskService.getTask(id));
    }

    @PostMapping
    public ResponseEntity<TaskResource> saveTask(@RequestBody TaskResource taskResource){
        return ResponseEntity.ok(taskService.saveTask(taskResource));
    }

    @PutMapping(value = "/updateDetails")
    public ResponseEntity<TaskResource> updateDetails(@RequestBody TaskResource taskResource){
        return ResponseEntity.ok(taskService.updateDetails(taskResource));
    }

    @PutMapping(value = "/updateStatus/{id}")
    public ResponseEntity<TaskResource> updateStatus(@PathVariable String id, @RequestParam TaskStatus taskStatus){
        return ResponseEntity.ok(taskService.updateStatus(id, taskStatus));
    }

    @PutMapping(value = "/updateCategory/{id}")
    public ResponseEntity<TaskResource> updateCategory(@PathVariable String id, @RequestParam String categoryName){
        return ResponseEntity.ok(taskService.updateCategory(id, categoryName));
    }
}
