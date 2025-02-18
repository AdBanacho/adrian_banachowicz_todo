package ch.cern.todo.category;

import ch.cern.todo.category.dataModels.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {

    @Query("SELECT c FROM Category c WHERE c.processedTo = '9999-12-31 12:00:00'")
    List<Category> findAllByProcessedTo();

    @Query("SELECT c FROM Category c WHERE c.name = :categoryName AND c.processedTo = '9999-12-31 12:00:00'")
    Optional<Category> findByCategoryNameAndProcessedTo(String categoryName);
}
