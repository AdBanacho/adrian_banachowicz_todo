package ch.cern.todo.tasks;

import ch.cern.todo.searchEngine.SearchCriteria;
import ch.cern.todo.tasks.dataModels.Task;
import ch.cern.todo.tasks.dataModels.TaskResource;
import ch.cern.todo.tasks.dataModels.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing tasks.
 * <p>
 * This interface provides methods for retrieving, creating, updating, and deleting tasks.
 * It also defines a method for mapping task entities to their resource representations with full user names.
 * </p>
 */
public interface TaskService {

    /**
     * Retrieves a paginated list of tasks filtered by the provided search criteria.
     *
     * @param pageable           the pagination and sorting configuration
     * @param searchCriteriaList a list of search criteria to filter the tasks
     * @return a {@link org.springframework.data.domain.Page} containing {@link TaskResource} objects
     */
    Page<TaskResource> getAllTasks(Pageable pageable, List<SearchCriteria> searchCriteriaList);

    /**
     * Retrieves a single task based on its unique identifier.
     *
     * @param id the unique identifier of the task
     * @return a {@link TaskResource} representing the task, or {@code null} if not found
     */
    TaskResource getTask(String id);

    /**
     * Maps a {@link Task} entity to a {@link TaskResource} that includes full user names for assigned and reported users.
     *
     * @param task the task entity to map
     * @return a {@link TaskResource} representing the task with full user details
     */
    TaskResource mapToResourceWithFullNames(Task task);

    /**
     * Saves a new task.
     *
     * @param taskResource the task resource containing details of the task to be created
     * @return a {@link TaskResource} representing the saved task with its generated identifier
     */
    TaskResource saveTask(TaskResource taskResource);

    /**
     * Updates the details of an existing task.
     *
     * @param taskResource the task resource containing the updated details; must include a valid identifier
     * @return a {@link TaskResource} representing the updated task
     */
    TaskResource updateDetails(TaskResource taskResource);

    /**
     * Updates the status of an existing task.
     *
     * @param id         the unique identifier of the task
     * @param taskStatus the new status to be set for the task
     * @return a {@link TaskResource} representing the updated task
     */
    TaskResource updateStatus(String id, TaskStatus taskStatus);

    /**
     * Updates the category associated with a task.
     *
     * @param id           the unique identifier of the task
     * @param categoryName the new category name to associate with the task
     * @return a {@link TaskResource} representing the updated task
     */
    TaskResource updateCategory(String id, String categoryName);

    /**
     * Deletes the task identified by the given unique identifier.
     *
     * @param id the unique identifier of the task to be deleted
     */
    void deleteTask(String id);
}

