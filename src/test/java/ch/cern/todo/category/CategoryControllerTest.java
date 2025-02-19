package ch.cern.todo.category;

import ch.cern.todo.category.dataModels.CategoryResource;
import ch.cern.todo.searchEngine.SearchCriteria;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
// Use an admin user for endpoints that require ADMIN role.
@WithMockUser(username = "admin", roles = {"ADMIN"})
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test GET /category endpoint.
     */
    @Test
    void testGetAllCategories() throws Exception {
        List<SearchCriteria> criteriaList = List.of(new SearchCriteria("name", "LIKE", "Electronics"));
        String criteriaJson = objectMapper.writeValueAsString(criteriaList);

        mockMvc.perform(get("/category")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("ascending", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(criteriaJson))
                .andExpect(status().isOk())
                // Expect that the JSON response contains a "content" array.
                .andExpect(jsonPath("$.content").isArray());
    }

    /**
     * Test POST /category endpoint (saveCategory).
     */
    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void testSaveCategory() throws Exception {
        // Create a new category resource (id is null so that a new ID is generated).
        CategoryResource newCategory = new CategoryResource(null, "Books", "Books description", Collections.emptyList());
        String jsonRequest = objectMapper.writeValueAsString(newCategory);

        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Books"))
                // Check that an "id" field is returned (it should be a valid UUID).
                .andExpect(jsonPath("$.id").exists());
    }

    /**
     * Test PUT /category/updateDetails endpoint.
     * This test first creates a category, then updates its details.
     */
    @Test
    void testUpdateDetails() throws Exception {
        // First, create a new category.
        CategoryResource newCategory = new CategoryResource(null, "InitialName", "Initial description", Collections.emptyList());
        String createJson = objectMapper.writeValueAsString(newCategory);

        String createResponse = mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Extract the generated ID from the response.
        String categoryId = objectMapper.readTree(createResponse).get("id").asText();

        // Build an updated category resource using the returned ID.
        CategoryResource updatedCategory = new CategoryResource(categoryId, "UpdatedBooks", "Updated description", Collections.emptyList());
        String updateJson = objectMapper.writeValueAsString(updatedCategory);

        mockMvc.perform(put("/category/updateDetails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedBooks"));
    }

    /**
     * Test PUT /category/deleteCategory/{id} endpoint.
     * This test first creates a new category and then deletes it.
     */
    @Test
    void testDeleteCategory() throws Exception {
        // Create a new category.
        CategoryResource newCategory = new CategoryResource(null, "DeleteTest", "Category to be deleted", Collections.emptyList());
        String createJson = objectMapper.writeValueAsString(newCategory);

        String createResponse = mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Extract the generated category ID.
        String categoryId = objectMapper.readTree(createResponse).get("id").asText();

        // Now call the delete endpoint using the generated category ID.
        mockMvc.perform(put("/category/deleteCategory/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
