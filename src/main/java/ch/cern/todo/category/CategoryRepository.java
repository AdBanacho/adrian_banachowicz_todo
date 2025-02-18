package ch.cern.todo.category;

import ch.cern.todo.category.dataModels.Category;
import ch.cern.todo.category.dataModels.CategoryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, CategoryKey> {

    String INFINITE_TIME = "CAST('9999-12-31 12:00:00' AS timestamp)";

    @Query("SELECT c FROM Category c WHERE c.processedTo = " + INFINITE_TIME)
    List<Category> findAllByProcessedTo();

    @Query("SELECT c FROM Category c WHERE c.name = :categoryName AND c.processedTo = " + INFINITE_TIME)
    Optional<Category> findByCategoryNameAndProcessedTo(String categoryName);

    @Query("SELECT c FROM Category c WHERE c.id = :id AND c.processedTo = " + INFINITE_TIME)
    Optional<Category> findByIdAndProcessedTo(String id);
}
