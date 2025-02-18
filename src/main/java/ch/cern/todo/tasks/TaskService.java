package ch.cern.todo.tasks;

import ch.cern.todo.tasks.dataModels.TaskResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskService {
    Page<TaskResource> getAllTasks(Pageable pageable);

    TaskResource getTask(String id);
}
