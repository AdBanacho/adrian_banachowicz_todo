package ch.cern.todo.tasks;

import ch.cern.todo.category.CategoryRepository;
import ch.cern.todo.category.dataModels.Category;
import ch.cern.todo.exceptions.ValidationException;
import ch.cern.todo.profile.ProfileService;
import ch.cern.todo.searchEngine.SearchCriteria;
import ch.cern.todo.searchEngine.TaskSearchEngineService;
import ch.cern.todo.tasks.dataModels.Task;
import ch.cern.todo.tasks.dataModels.TaskResource;
import ch.cern.todo.tasks.dataModels.TaskStatus;
import ch.cern.todo.validation.InputFieldValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private static final String TASK = "Task";
    private static final String CATEGORY = "Category";
    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, CategoryRepository categoryRepository, ProfileService profileService) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
        this.profileService = profileService;
    }

    @Override
    public Page<TaskResource> getAllTasks(Pageable pageable, List<SearchCriteria> searchCriteriaList) {
        logger.debug("Entering getAllTasks with pageable: {} and search criteria: {}", pageable, searchCriteriaList);
        Specification<Task> baseSpec = (root, query, builder) -> builder.and(
                builder.notEqual(root.get("status"), TaskStatus.DELETED),
                builder.equal(root.get("processedTo"), Timestamp.valueOf("9999-12-31 12:00:00"))
        );

        Specification<Task> dynamicSpec = Specification.where(null);
        for (SearchCriteria criteria : searchCriteriaList) {
            dynamicSpec = dynamicSpec.and(new TaskSearchEngineService(criteria));
            logger.debug("Added search spec for criteria: {}", criteria);
        }

        Page<Task> tasks = taskRepository.findAll(baseSpec.and(dynamicSpec), pageable);
        logger.info("Found {} tasks", tasks.getTotalElements());
        return tasks.map(this::mapToResourceWithFullNames);
    }

    @Override
    public TaskResource getTask(String id) {
        logger.debug("Retrieving task with ID: {}", id);
        TaskResource result = taskRepository.findByIdAndProcessedTo(id)
                .map(this::mapToResourceWithFullNames)
                .orElse(null);
        if (result == null) {
            logger.warn("Task with ID {} not found", id);
        }
        return result;
    }

    @Override
    public TaskResource mapToResourceWithFullNames(Task task) {
        logger.debug("Mapping task {} to resource with full names", task.getId());
        String assignedToName = profileService.getFullName(task.getAssignedTo());
        String reportedByName = profileService.getFullName(task.getReportedBy());
        TaskResource resource = task.transferToResource(assignedToName, reportedByName);
        logger.debug("Mapped task {} to resource", task.getId());
        return resource;
    }

    @Override
    public TaskResource saveTask(TaskResource taskResource) {
        logger.debug("Saving task with details: {}", taskResource);
        Category category = categoryRepository.findByCategoryNameAndProcessedTo(taskResource.categoryName()).orElse(null);
        logger.debug("Category lookup for name {} returned: {}", taskResource.categoryName(), category);
        validateNewTaskInput(taskResource, category);
        Task taskToSave = taskResource.transferToNewEntity(TaskStatus.CREATED, category);
        Task savedTask = taskRepository.save(taskToSave);
        logger.info("Task saved with ID: {}", savedTask.getId());
        return mapToResourceWithFullNames(savedTask);
    }

    private void validateNewTaskInput(TaskResource taskResource, Category category) {
        List<String> errorMessages = new ArrayList<>();
        String taskId = taskResource.id();
        String existingTaskId = taskRepository.findByIdAndProcessedTo(taskId).map(Task::getId).orElse(null);
        InputFieldValidator.validateIfEntityExists(TASK, "id", existingTaskId);
        InputFieldValidator.validateIfNotEntityExists(CATEGORY, taskResource.categoryName(), category);
        InputFieldValidator.validateFieldNotEmpty(TASK, "name", taskResource.name(), errorMessages);
        InputFieldValidator.validateFieldNotEmpty(TASK, "categoryName", taskResource.categoryName(), errorMessages);
        InputFieldValidator.validateFieldNotEmpty(TASK, "reportedBy", taskResource.reportedBy(), errorMessages);
        InputFieldValidator.validateTimeIfNotInPast("Deadline", taskResource.deadLine(), errorMessages);
        if (!errorMessages.isEmpty()) {
            logger.error("Validation errors for new task: {}", errorMessages);
            throw new ValidationException(String.join(", \n", errorMessages));
        }
    }

    @Override
    public TaskResource updateDetails(TaskResource taskResource) {
        logger.debug("Updating details for task with ID: {}", taskResource.id());
        Task existingTask = taskRepository.findByIdAndProcessedTo(taskResource.id()).orElse(null);
        validateUpdatingTaskInput(taskResource, existingTask);
        ProfileService.validationSameUser(Arrays.asList(taskResource.reportedBy(), taskResource.assignedTo()), "Task details");
        Task taskToUpdate = taskResource.transferToExistingEntity(existingTask);
        TaskResource updatedResource = updateTask(existingTask, taskToUpdate);
        logger.info("Task with ID {} updated successfully", taskResource.id());
        return updatedResource;
    }

    private void validateUpdatingTaskInput(TaskResource taskResource, Task existingTask) {
        List<String> errorMessages = new ArrayList<>();
        InputFieldValidator.validateIfNotEntityExists(TASK, taskResource.id(), existingTask);
        InputFieldValidator.validateIfFieldChanged(existingTask.getCategory().getName(), taskResource.categoryName(), errorMessages);
        InputFieldValidator.validateIfFieldChanged(existingTask.getReportedBy(), taskResource.reportedBy(), errorMessages);
        InputFieldValidator.validateFieldNotEmpty(TASK, "name", taskResource.name(), errorMessages);
        InputFieldValidator.validateTimeIfNotInPast("Deadline", taskResource.deadLine(), errorMessages);
        if (!errorMessages.isEmpty()) {
            logger.error("Validation errors during task update: {}", errorMessages);
            throw new ValidationException(String.join(", \n", errorMessages));
        }
    }

    @Override
    public TaskResource updateStatus(String id, TaskStatus taskStatus) {
        logger.debug("Updating status for task ID: {} to {}", id, taskStatus);
        Task existingTask = taskRepository.findByIdAndProcessedTo(id).orElse(null);
        InputFieldValidator.validateIfNotEntityExists(TASK, id, existingTask);
        Task taskWithUpdatedStatus = existingTask.updateStatus(taskStatus);
        TaskResource updatedResource = updateTask(existingTask, taskWithUpdatedStatus);
        logger.info("Task ID {} status updated to {}", id, taskStatus);
        return updatedResource;
    }

    @Override
    public TaskResource updateCategory(String id, String categoryName) {
        logger.debug("Updating category for task ID: {} to category: {}", id, categoryName);
        Task existingTask = taskRepository.findByIdAndProcessedTo(id).orElse(null);
        Category category = categoryRepository.findByCategoryNameAndProcessedTo(categoryName).orElse(null);
        InputFieldValidator.validateIfNotEntityExists(TASK, id, existingTask);
        InputFieldValidator.validateIfNotEntityExists(CATEGORY, categoryName, category);
        Task taskWithUpdatedCategory = existingTask.updateCategory(category);
        TaskResource updatedResource = updateTask(existingTask, taskWithUpdatedCategory);
        logger.info("Task ID {} category updated to {}", id, categoryName);
        return updatedResource;
    }

    private TaskResource updateTask(Task existingTask, Task taskWithUpdatedStatus) {
        Task updatedTask = saveUpdatedTask(existingTask, taskWithUpdatedStatus);
        return mapToResourceWithFullNames(updatedTask);
    }

    @Override
    public void deleteTask(String id) {
        logger.debug("Deleting task with ID: {}", id);
        Task existingTask = taskRepository.findByIdAndProcessedTo(id).orElse(null);
        InputFieldValidator.validateIfNotEntityExists(TASK, id, existingTask);
        Task taskWithUpdatedStatus = existingTask.updateStatus(TaskStatus.DELETED);
        saveUpdatedTask(existingTask, taskWithUpdatedStatus);
        logger.info("Task with ID {} marked as deleted", id);
    }

    private Task saveUpdatedTask(Task existingTask, Task taskWithUpdatedData) {
        logger.debug("Saving updated task data for task ID: {}", existingTask.getId());
        closeTaskEntity(existingTask);
        Task savedTask = taskRepository.save(taskWithUpdatedData);
        logger.debug("Updated task saved with ID: {}", savedTask.getId());
        return savedTask;
    }

    private void closeTaskEntity(Task existingTask) {
        logger.debug("Closing task entity for task ID: {}", existingTask.getId());
        existingTask.closeTaskEntity();
        taskRepository.save(existingTask);
        logger.debug("Task entity for task ID {} closed", existingTask.getId());
    }

}