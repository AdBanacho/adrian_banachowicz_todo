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

    String INFINITE_TIME = "c.processedTo = CAST('9999-12-31 12:00:00' AS timestamp)";
    String STATUS_NOT_DELETED = "c.status <> CategoryStatus.DELETED";
    String ACTIVE_CATEGORIES = STATUS_NOT_DELETED + " AND " + INFINITE_TIME;

    @Query("SELECT c FROM Category c WHERE " + ACTIVE_CATEGORIES)
    List<Category> findAllByProcessedTo();

    @Query("SELECT c FROM Category c WHERE c.name = :categoryName AND " + ACTIVE_CATEGORIES)
    Optional<Category> findByCategoryNameAndProcessedTo(String categoryName);

    @Query("SELECT c FROM Category c WHERE c.id = :id AND " + ACTIVE_CATEGORIES)
    Optional<Category> findByIdAndProcessedTo(String id);
}
