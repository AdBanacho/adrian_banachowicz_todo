package ch.cern.todo.tasks;

import ch.cern.todo.tasks.dataModels.Task;
import ch.cern.todo.tasks.dataModels.TaskResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    Page<TaskResource> getAllTasks(Pageable pageable);

    TaskResource getTask(String id);

    TaskResource mapToResourceWithFullNames(Task task);
}
