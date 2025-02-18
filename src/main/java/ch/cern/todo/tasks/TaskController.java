package ch.cern.todo.tasks;

import ch.cern.todo.tasks.dataModels.TaskResource;
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
}
