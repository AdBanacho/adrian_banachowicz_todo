package ch.cern.todo.tasks;

import ch.cern.todo.category.CategoryRepository;
import ch.cern.todo.category.dataModels.Category;
import ch.cern.todo.exceptions.EntityNotExistException;
import ch.cern.todo.exceptions.ValidationException;
import ch.cern.todo.profile.ProfileService;
import ch.cern.todo.tasks.dataModels.Task;
import ch.cern.todo.tasks.dataModels.TaskResource;
import ch.cern.todo.tasks.dataModels.TaskStatus;
import ch.cern.todo.validation.InputFieldValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private static final String TASK = "Task";
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
    public Page<TaskResource> getAllTasks(Pageable pageable) {
        Page<Task> tasks = taskRepository.findAllByProcessedTo(pageable);
        return tasks.map(this::mapToResourceWithFullNames);
    }

    @Override
    public TaskResource getTask(String id) {
        return taskRepository.findByIdAndProcessedTo(id).map(this::mapToResourceWithFullNames).orElse(null);
    }

    @Override
    public TaskResource mapToResourceWithFullNames(Task task) {
        String assignedToName = profileService.getFullName(task.getAssignedTo());
        String reportedByName = profileService.getFullName(task.getReportedBy());
        return task.transferToResource(assignedToName, reportedByName);
    }

    @Override
    public TaskResource saveTask(TaskResource taskResource) {
        Category category = categoryRepository.findByCategoryNameAndProcessedTo(taskResource.categoryName()).orElse(null);
        validateTaskInput(taskResource, category);
        Task taskToSave = taskResource.transferToEntity(TaskStatus.CREATED, category);
        Task savedTask = taskRepository.save(taskToSave);

        return mapToResourceWithFullNames(savedTask);
    }

    private void validateTaskInput(TaskResource taskResource, Category category) {
        List<String> errorMessages = new ArrayList<>();
        String taskId = taskResource.id();
        String existingTaskId = taskRepository.findByIdAndProcessedTo(taskId).map(Task::getId).orElse(null);
        InputFieldValidator.validateIfEntityExists("Task", "id", existingTaskId);

        if (category == null){
            String categoryName = taskResource.categoryName().isEmpty() ? "" : taskResource.categoryName();
            throw new EntityNotExistException(("Category \"" + categoryName + "\" not exists"));
        }

        InputFieldValidator.validateFieldNotEmpty(TASK, "name", taskResource.name(), errorMessages);
        InputFieldValidator.validateFieldNotEmpty(TASK, "categoryName", taskResource.categoryName(), errorMessages);
        InputFieldValidator.validateFieldNotEmpty(TASK, "reportedBy", taskResource.reportedBy(), errorMessages);
        InputFieldValidator.validateTimeIfNotInPast("Deadline", taskResource.deadLine(), errorMessages);
        if (!errorMessages.isEmpty()) {
            throw new ValidationException(String.join(", \n", errorMessages));
        }
    }

}