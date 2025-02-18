package ch.cern.todo.tasks;

import ch.cern.todo.profile.ProfileService;
import ch.cern.todo.tasks.dataModels.Task;
import ch.cern.todo.tasks.dataModels.TaskResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProfileService profileService;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, ProfileService profileService) {
        this.taskRepository = taskRepository;
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
}