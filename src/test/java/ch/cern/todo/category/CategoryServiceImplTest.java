package ch.cern.todo.category;

import ch.cern.todo.category.dataModels.Category;
import ch.cern.todo.category.dataModels.CategoryResource;
import ch.cern.todo.category.dataModels.CategoryStatus;
import ch.cern.todo.exceptions.EntityAlreadyExistsException;
import ch.cern.todo.searchEngine.SearchCriteria;
import ch.cern.todo.tasks.TaskService;
import ch.cern.todo.tasks.dataModels.Task;
import ch.cern.todo.tasks.dataModels.TaskResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private CategoryServiceImpl categoryServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ===== Test getAllCategories =====

    @Test
    void testGetAllCategories() {
        List<SearchCriteria> criteriaList = Collections.singletonList(
                new SearchCriteria("name", "LIKE", "Electronics")
        );

        // Create a dummy category and stub its mapping.
        Category category = mock(Category.class);
        CategoryResource dummyResource = new CategoryResource("1", "Electronics", "Some description", Collections.emptyList());
        when(category.transferToResource()).thenReturn(dummyResource);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> page = new PageImpl<>(List.of(category), pageable, 1);
        when(categoryRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<CategoryResource> result = categoryServiceImpl.getAllCategories(pageable, criteriaList);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        CategoryResource res = result.getContent().get(0);
        assertEquals("Electronics", res.name());
    }

    // ===== Test getCategory =====

    @Test
    void testGetCategory_Exists() {
        Category category = mock(Category.class);
        Task task = mock(Task.class);
        TaskResource dummyTaskResource = mock(TaskResource.class);
        when(taskService.mapToResourceWithFullNames(task)).thenReturn(dummyTaskResource);
        when(category.getTasks()).thenReturn(List.of(task));

        CategoryResource dummyCategoryResource = new CategoryResource("1", "Electronics", "Description", List.of(dummyTaskResource));
        when(category.transferToResource(anyList())).thenReturn(dummyCategoryResource);

        when(categoryRepository.findByCategoryNameAndProcessedTo("Electronics")).thenReturn(Optional.of(category));
        CategoryResource result = categoryServiceImpl.getCategory("Electronics");
        assertNotNull(result);
        assertEquals("Electronics", result.name());
    }

    @Test
    void testGetCategory_NotExists() {
        when(categoryRepository.findByCategoryNameAndProcessedTo("Nonexistent")).thenReturn(Optional.empty());
        CategoryResource result = categoryServiceImpl.getCategory("Nonexistent");
        assertNull(result);
    }

    // ===== Test saveCategory =====

    @Test
    void testSaveCategory_Success() {
        CategoryResource inputResource = new CategoryResource(null, "Books", "Description for Books", null);
        when(categoryRepository.findByCategoryNameAndProcessedTo("Books")).thenReturn(Optional.empty());

        // Use a spy so we can stub the mapping method.
        CategoryResource spyResource = Mockito.spy(inputResource);
        Category dummyCategory = mock(Category.class);
        doReturn(dummyCategory).when(spyResource).transferToNewEntity();
        when(categoryRepository.save(any(Category.class))).thenReturn(dummyCategory);

        CategoryResource savedResource = new CategoryResource("1", "Books", "Description for Books", Collections.emptyList());
        when(dummyCategory.transferToResource()).thenReturn(savedResource);

        CategoryResource result = categoryServiceImpl.saveCategory(spyResource);
        assertNotNull(result);
        assertEquals("Books", result.name());
    }

    @Test
    void testSaveCategory_ValidationException_DuplicateName() {
        // Create a CategoryResource with duplicate name.
        CategoryResource inputResource = new CategoryResource(null, "Electronics", "Desc", null);

        // Simulate that an existing category is found.
        Category existingCategory = mock(Category.class);
        // IMPORTANT: Stub getName() so that it returns the duplicate name.
        when(existingCategory.getName()).thenReturn("Electronics");
        when(categoryRepository.findByCategoryNameAndProcessedTo("Electronics")).thenReturn(Optional.of(existingCategory));

        // Expect a ValidationException due to duplicate name.
        assertThrows(EntityAlreadyExistsException.class, () -> categoryServiceImpl.saveCategory(inputResource));
    }

    // ===== Test updateDetails =====

    @Test
    void testUpdateDetails_Success() {
        CategoryResource inputResource = new CategoryResource("1", "UpdatedBooks", "New Description", null);
        CategoryResource spyResource = Mockito.spy(inputResource);

        Category existingCategory = mock(Category.class);
        when(categoryRepository.findByIdAndProcessedTo("1")).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findByCategoryNameAndProcessedTo("UpdatedBooks")).thenReturn(Optional.of(existingCategory));

        Category updatedCategory = mock(Category.class);
        doReturn(updatedCategory).when(spyResource).transferToExistingEntity(existingCategory);
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);

        CategoryResource updatedResource = new CategoryResource("1", "UpdatedBooks", "New Description", Collections.emptyList());
        when(updatedCategory.transferToResource()).thenReturn(updatedResource);

        CategoryResource result = categoryServiceImpl.updateDetails(spyResource);
        assertNotNull(result);
        assertEquals("UpdatedBooks", result.name());
    }

    // ===== Test deleteCategory =====

    @Test
    void testDeleteCategory_Success() {
        Category existingCategory = mock(Category.class);
        Task task1 = mock(Task.class);
        when(task1.getId()).thenReturn("t1");
        when(existingCategory.getTasks()).thenReturn(List.of(task1));
        when(categoryRepository.findByIdAndProcessedTo("1")).thenReturn(Optional.of(existingCategory));

        Category deletedCategory = mock(Category.class);
        when(existingCategory.updateStatus(CategoryStatus.DELETED)).thenReturn(deletedCategory);
        doNothing().when(existingCategory).closeCategoryEntity();
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);
        when(categoryRepository.save(deletedCategory)).thenReturn(deletedCategory);

        doNothing().when(taskService).deleteTask("t1");

        assertDoesNotThrow(() -> categoryServiceImpl.deleteCategory("1"));
        verify(taskService, times(1)).deleteTask("t1");
        verify(categoryRepository, atLeastOnce()).save(any(Category.class));
    }
}
