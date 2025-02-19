package ch.cern.todo.tasks;

import ch.cern.todo.searchEngine.SearchCriteria;
import ch.cern.todo.tasks.dataModels.Task;
import ch.cern.todo.tasks.dataModels.TaskResource;
import ch.cern.todo.tasks.dataModels.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskService {
    Page<TaskResource> getAllTasks(Pageable pageable,  List<SearchCriteria> searchCriteriaList);

    TaskResource getTask(String id);

    TaskResource mapToResourceWithFullNames(Task task);

    TaskResource saveTask(TaskResource taskResource);

    TaskResource updateDetails(TaskResource taskResource);

    TaskResource updateStatus(String id, TaskStatus taskStatus);

    TaskResource updateCategory(String id, String categoryName);

    void deleteTask(String id);
}
