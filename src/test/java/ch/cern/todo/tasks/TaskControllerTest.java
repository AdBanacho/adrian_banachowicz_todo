package ch.cern.todo.tasks;

import ch.cern.todo.searchEngine.SearchCriteria;
import ch.cern.todo.tasks.dataModels.TaskPriorityStatus;
import ch.cern.todo.tasks.dataModels.TaskResource;
import ch.cern.todo.tasks.dataModels.TaskStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Helper method: Create a category via POST /category.
     * Returns the created category's name (or you could extract its id if needed).
     */
    private void createCategory(String categoryName, String description) throws Exception {
        // Build a CategoryResource.
        // Adjust the record constructor if needed. We assume: (id, name, description, tasks)
        String categoryJson = objectMapper.writeValueAsString(
                new ch.cern.todo.category.dataModels.CategoryResource(null, categoryName, description, Collections.emptyList())
        );
        // Perform POST /category as an ADMIN (since that endpoint requires ADMIN).
        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(categoryName));
    }

    /**
     * Test GET /task endpoint.
     */
    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void testGetAllTasks() throws Exception {
        // Pre-create a category so that tasks can be linked.
        createCategory("CategoryForTasks", "Category for task tests");

        // Create a dummy search criteria list.
        List<SearchCriteria> criteriaList = List.of(new SearchCriteria("assignedTo", "=", "user1"));
        String criteriaJson = objectMapper.writeValueAsString(criteriaList);

        mockMvc.perform(get("/task")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("ascending", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(criteriaJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    /**
     * Test GET /task/{id} endpoint.
     * Create a task first and then retrieve it.
     */
    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void testGetTask() throws Exception {
        // Pre-create the category that this task will reference.
        createCategory("CategoryA", "Category A description");

        // Create a new task using the POST /task endpoint.
        TaskResource newTask = new TaskResource(
                null,
                "Test Task",
                "Test Description",
                Timestamp.valueOf("2025-03-01 00:00:00"),
                TaskStatus.CREATED,
                TaskPriorityStatus.MEDIUM,
                "user1",
                "User One",
                "user2",
                "User Two",
                "CategoryA"
        );
        String createJson = objectMapper.writeValueAsString(newTask);

        String createResponse = mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        String taskId = objectMapper.readTree(createResponse).get("id").asText();

        // Now, retrieve the task.
        mockMvc.perform(get("/task/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.name").value("Test Task"));
    }

    /**
     * Test POST /task endpoint (saveTask).
     */
    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void testSaveTask() throws Exception {
        // Pre-create category "CategoryB" so that the task's category exists.
        createCategory("CategoryB", "Description for CategoryB");

        TaskResource newTask = new TaskResource(
                null,
                "New Task",
                "New task description",
                Timestamp.valueOf("2025-03-05 00:00:00"),
                TaskStatus.CREATED,
                TaskPriorityStatus.HIGH,
                "user1",
                "User One",
                "user2",
                "User Two",
                "CategoryB"
        );
        String jsonRequest = objectMapper.writeValueAsString(newTask);

        mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Task"))
                .andExpect(jsonPath("$.id").exists());
    }

    /**
     * Test PUT /task/updateDetails endpoint.
     */
    @Test
    @WithMockUser(username = "user1", roles = {"ADMIN"})
    void testUpdateDetails() throws Exception {
        // Pre-create category "CategoryC" for this test.
        createCategory("CategoryC", "Description for CategoryC");

        // Create a new task.
        TaskResource newTask = new TaskResource(
                null,
                "Initial Task",
                "Initial description",
                Timestamp.valueOf("2025-03-10 00:00:00"),
                TaskStatus.CREATED,
                TaskPriorityStatus.MEDIUM,
                "user1",
                "User One",
                "user2",
                "User Two",
                "CategoryC"
        );
        String createJson = objectMapper.writeValueAsString(newTask);

        String createResponse = mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String taskId = objectMapper.readTree(createResponse).get("id").asText();

        // Update the task details.
        TaskResource updatedTask = new TaskResource(
                taskId,
                "Updated Task",
                "Updated description",
                Timestamp.valueOf("2025-03-15 00:00:00"),
                TaskStatus.CREATED,
                TaskPriorityStatus.MEDIUM,
                "user1",
                "User One",
                "user2",
                "User Two",
                "CategoryC"
        );
        String updateJson = objectMapper.writeValueAsString(updatedTask);

        mockMvc.perform(put("/task/updateDetails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Task"));
    }

    /**
     * Test PUT /task/updateStatus/{id} endpoint.
     */
    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void testUpdateStatus() throws Exception {
        // Pre-create category "CategoryD" for this test.
        createCategory("CategoryD", "Description for CategoryD");

        // Create a new task.
        TaskResource newTask = new TaskResource(
                null,
                "Status Change Task",
                "Task description",
                Timestamp.valueOf("2025-03-20 00:00:00"),
                TaskStatus.CREATED,
                TaskPriorityStatus.LOW,
                "user1",
                "User One",
                "user2",
                "User Two",
                "CategoryD"
        );
        String createJson = objectMapper.writeValueAsString(newTask);

        String createResponse = mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String taskId = objectMapper.readTree(createResponse).get("id").asText();

        // Update the status to COMPLETED.
        mockMvc.perform(put("/task/updateStatus/" + taskId)
                        .param("taskStatus", "COMPLETED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    /**
     * Test PUT /task/updateCategory/{id} endpoint.
     * This endpoint requires ADMIN role.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateCategory() throws Exception {
        // Pre-create two categories:
        // One for the task's current category ("OldCategory") and one for the new category ("NewCategory").
        createCategory("OldCategory", "Old category description");
        createCategory("NewCategory", "New category description");

        // Create a new task with category "OldCategory".
        TaskResource newTask = new TaskResource(
                null,
                "Category Change Task",
                "Task description",
                Timestamp.valueOf("2025-03-25 00:00:00"),
                TaskStatus.CREATED,
                TaskPriorityStatus.MEDIUM,
                "user1",
                "User One",
                "user2",
                "User Two",
                "OldCategory"
        );
        String createJson = objectMapper.writeValueAsString(newTask);

        String createResponse = mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String taskId = objectMapper.readTree(createResponse).get("id").asText();

        // Update the task's category to "NewCategory".
        mockMvc.perform(put("/task/updateCategory/" + taskId)
                        .param("categoryName", "NewCategory")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName").value("NewCategory"));
    }

    /**
     * Test PUT /task/deleteTask/{id} endpoint.
     * This endpoint requires ADMIN role.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteTask() throws Exception {
        // Pre-create category "CategoryE" for this test.
        createCategory("CategoryE", "Description for CategoryE");

        // Create a new task.
        TaskResource newTask = new TaskResource(
                null,
                "Task To Delete",
                "Task description",
                Timestamp.valueOf("2025-03-29 00:00:00"),
                TaskStatus.CREATED,
                TaskPriorityStatus.LOW,
                "user1",
                "User One",
                "user2",
                "User Two",
                "CategoryE"
        );
        String createJson = objectMapper.writeValueAsString(newTask);

        String createResponse = mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String taskId = objectMapper.readTree(createResponse).get("id").asText();

        // Now, delete the task.
        mockMvc.perform(put("/task/deleteTask/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
