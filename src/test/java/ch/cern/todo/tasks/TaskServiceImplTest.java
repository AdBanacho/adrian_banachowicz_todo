package ch.cern.todo.tasks;

import ch.cern.todo.category.CategoryRepository;
import ch.cern.todo.category.dataModels.Category;
import ch.cern.todo.exceptions.EntityNotExistException;
import ch.cern.todo.profile.ProfileService;
import ch.cern.todo.searchEngine.SearchCriteria;
import ch.cern.todo.tasks.dataModels.Task;
import ch.cern.todo.tasks.dataModels.TaskPriorityStatus;
import ch.cern.todo.tasks.dataModels.TaskResource;
import ch.cern.todo.tasks.dataModels.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private TaskServiceImpl taskServiceImpl;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set a dummy security context with a non-null authentication.
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(
                new UsernamePasswordAuthenticationToken("user1", "password", Collections.emptyList())
        );
        SecurityContextHolder.setContext(context);

    }

    @Test
    void testGetAllTasks() {
        // Prepare a dummy Task that meets the base specifications.
        Task task = new Task();
        task.setId("task1");
        task.setName("Test Task");
        task.setDescription("Test Task Description");
        task.setDeadLine(Timestamp.valueOf("2025-02-19 00:00:00"));
        task.setStatus(TaskStatus.CREATED); // Not DELETED.
        task.setProcessedTo(Timestamp.valueOf("9999-12-31 12:00:00"));
        task.setAssignedTo("user1");
        task.setReportedBy("user2");

        // Set a non-null category to avoid NPE
        Category category = new Category();
        category.setName("Category1");
        task.setCategory(category);

        // Mock profile service responses.
        when(profileService.getFullName("user1")).thenReturn("User One");
        when(profileService.getFullName("user2")).thenReturn("User Two");

        // Prepare a page with the dummy task.
        List<Task> tasks = Collections.singletonList(task);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(tasks, pageable, tasks.size());

        // When findAll is called with any specification, return our taskPage.
        when(taskRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(taskPage);

        // Create a dummy search criteria list.
        List<SearchCriteria> criteriaList = Collections.singletonList(
                new SearchCriteria("name", "LIKE", "Test")
        );

        // Call the service.
        Page<TaskResource> result = taskServiceImpl.getAllTasks(pageable, criteriaList);

        // Verify the result.
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        TaskResource resource = result.getContent().getFirst();
        assertEquals("task1", resource.id());
        assertEquals("Test Task", resource.name());
        assertEquals("User One", resource.assignedToName());
        assertEquals("User Two", resource.reportedByName());
    }

    @Test
    void testGetTask() {
        // Prepare a dummy Task.
        Task task = new Task();
        task.setId("task1");
        task.setName("Test Task");
        task.setDescription("Test Task Description");
        task.setProcessedTo(Timestamp.valueOf("9999-12-31 12:00:00"));
        task.setAssignedTo("user1");
        task.setReportedBy("user2");

        // Set a non-null category to avoid NPE when accessing category.getName()
        Category category = new Category();
        category.setName("Category1");
        task.setCategory(category);

        // Mock profile service responses.
        when(profileService.getFullName("user1")).thenReturn("User One");
        when(profileService.getFullName("user2")).thenReturn("User Two");
        when(taskRepository.findByIdAndProcessedTo("task1")).thenReturn(Optional.of(task));

        // Call the service.
        TaskResource resource = taskServiceImpl.getTask("task1");

        // Verify the result.
        assertNotNull(resource);
        assertEquals("task1", resource.id());
        assertEquals("Test Task", resource.name());
        // You may also verify categoryName if your mapping uses it.
    }

    @Test
    void testSaveTask_Success() {
        // Create a TaskResource with 11 arguments.
        TaskResource resource = new TaskResource(
                "task1",                          // id
                "Test Task",                      // name
                "Test Task Description",          // description
                Timestamp.valueOf("2025-02-20 00:00:00"), // deadLine
                TaskStatus.CREATED,               // status
                TaskPriorityStatus.MEDIUM,        // priorityStatus
                "user1",                          // assignedTo
                "User One",                       // assignedToName
                "user2",                          // reportedBy
                "User Two",                       // reportedByName
                "Category1"                       // categoryName
        );

        // Prepare a dummy Category.
        Category category = new Category();
        category.setName("Category1");
        when(categoryRepository.findByCategoryNameAndProcessedTo("Category1"))
                .thenReturn(Optional.of(category));

        // Simulate that no existing Task with the given ID exists.
        when(taskRepository.findByIdAndProcessedTo("task1")).thenReturn(Optional.empty());

        // Mock profile service responses.
        when(profileService.getFullName("user1")).thenReturn("User One");
        when(profileService.getFullName("user2")).thenReturn("User Two");

        // Prepare a dummy saved Task.
        Task savedTask = new Task();
        savedTask.setId("task1");
        savedTask.setName("Test Task");
        savedTask.setDescription("Test Task Description");
        savedTask.setCategory(category);
        savedTask.setAssignedTo("user1");
        savedTask.setReportedBy("user2");
        savedTask.setStatus(TaskStatus.CREATED);
        savedTask.setProcessedTo(Timestamp.valueOf("9999-12-31 12:00:00"));
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Call the service.
        TaskResource savedResource = taskServiceImpl.saveTask(resource);

        // Verify the result.
        assertNotNull(savedResource);
        assertEquals("task1", savedResource.id());
        assertEquals("Test Task", savedResource.name());
        assertEquals("User One", savedResource.assignedToName());
        assertEquals("User Two", savedResource.reportedByName());
    }

    @Test
    void testSaveTask_UnsuccessfulSave_MissingCategory() {
        // Create a TaskResource with 11 arguments.
        TaskResource resource = new TaskResource(
                "task2",
                "Test Task",
                "Test Task Description",
                Timestamp.valueOf("2025-02-20 00:00:00"),
                TaskStatus.CREATED,
                TaskPriorityStatus.MEDIUM,
                "user1",
                "User One",
                "user2",
                "User Two",
                "Category1"
        );

        // Simulate that no existing task with the given id exists.
        when(taskRepository.findByIdAndProcessedTo("task2")).thenReturn(Optional.empty());

        // Simulate that categoryRepository returns empty for the given category name.
        when(categoryRepository.findByCategoryNameAndProcessedTo("Category1")).thenReturn(Optional.empty());

        // Expect an EntityNotExistException due to missing category.
        assertThrows(EntityNotExistException.class, () -> {
            taskServiceImpl.saveTask(resource);
        });
    }

    // ===========================
    // Tests for update scenarios
    // ===========================


    @Test
    void testUpdateStatus_Success() {
        // Prepare an existing Task.
        Task existingTask = new Task();
        existingTask.setId("task1");
        existingTask.setStatus(TaskStatus.CREATED);
        existingTask.setProcessedTo(Timestamp.valueOf("9999-12-31 12:00:00"));
        existingTask.setAssignedTo("user1");
        existingTask.setReportedBy("user2");
        // Set non-null category.
        Category category = new Category();
        category.setName("Category1");
        existingTask.setCategory(category);

        when(taskRepository.findByIdAndProcessedTo("task1")).thenReturn(Optional.of(existingTask));

        // Simulate the updated task after status change.
        Task updatedTask = new Task();
        updatedTask.setId("task1");
        updatedTask.setStatus(TaskStatus.COMPLETED);
        updatedTask.setProcessedTo(Timestamp.valueOf("9999-12-31 12:00:00"));
        updatedTask.setAssignedTo("user1");
        updatedTask.setReportedBy("user2");
        updatedTask.setCategory(category);
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        // Call updateStatus.
        TaskResource result = taskServiceImpl.updateStatus("task1", TaskStatus.COMPLETED);

        assertNotNull(result);
        assertEquals(TaskStatus.COMPLETED, result.status());
    }

    @Test
    void testUpdateCategory_Success() {
        // Prepare an existing Task.
        Task existingTask = new Task();
        existingTask.setId("task1");
        existingTask.setStatus(TaskStatus.CREATED);
        existingTask.setProcessedTo(Timestamp.valueOf("9999-12-31 12:00:00"));
        existingTask.setAssignedTo("user1");
        existingTask.setReportedBy("user2");
        // Existing category.
        Category oldCategory = new Category();
        oldCategory.setName("OldCategory");
        existingTask.setCategory(oldCategory);

        when(taskRepository.findByIdAndProcessedTo("task1")).thenReturn(Optional.of(existingTask));
        // New category to update.
        Category newCategory = new Category();
        newCategory.setName("NewCategory");
        when(categoryRepository.findByCategoryNameAndProcessedTo("NewCategory"))
                .thenReturn(Optional.of(newCategory));

        // Simulate updated task with new category.
        Task updatedTask = new Task();
        updatedTask.setId("task1");
        updatedTask.setStatus(TaskStatus.CREATED);
        updatedTask.setProcessedTo(Timestamp.valueOf("9999-12-31 12:00:00"));
        updatedTask.setAssignedTo("user1");
        updatedTask.setReportedBy("user2");
        updatedTask.setCategory(newCategory);
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        // Call updateCategory.
        TaskResource result = taskServiceImpl.updateCategory("task1", "NewCategory");

        assertNotNull(result);
        // Verify that the category name is updated via the mapping.
        assertEquals("NewCategory", result.categoryName());
    }

    // ===========================
    // Test for deletion
    // ===========================

    @Test
    void testDeleteTask_Success() {
        // Prepare an existing Task.
        Task existingTask = new Task();
        existingTask.setId("task1");
        existingTask.setStatus(TaskStatus.CREATED);
        existingTask.setProcessedTo(Timestamp.valueOf("9999-12-31 12:00:00"));
        existingTask.setAssignedTo("user1");
        existingTask.setReportedBy("user2");
        // Set a non-null category.
        Category category = new Category();
        category.setName("Category1");
        existingTask.setCategory(category);

        when(taskRepository.findByIdAndProcessedTo("task1")).thenReturn(Optional.of(existingTask));

        // Simulate the task after deletion (status DELETED).
        Task deletedTask = new Task();
        deletedTask.setId("task1");
        deletedTask.setStatus(TaskStatus.DELETED);
        deletedTask.setProcessedTo(Timestamp.valueOf("9999-12-31 12:00:00"));
        deletedTask.setAssignedTo("user1");
        deletedTask.setReportedBy("user2");
        deletedTask.setCategory(category);
        when(taskRepository.save(any(Task.class))).thenReturn(deletedTask);

        // Call deleteTask.
        assertDoesNotThrow(() -> taskServiceImpl.deleteTask("task1"));

        // Verify that save was called (indicating an update).
        verify(taskRepository, atLeastOnce()).save(any(Task.class));
    }
}
