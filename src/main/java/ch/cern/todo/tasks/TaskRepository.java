package ch.cern.todo.tasks;

import ch.cern.todo.tasks.dataModels.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, String> {

    @Query("SELECT t FROM Task t WHERE t.processedTo = '9999-12-31 12:00:00'")
    Page<Task> findAllByProcessedTo(Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.id = :id AND t.processedTo = '9999-12-31 12:00:00'")
    Optional<Task> findByIdAndProcessedTo(String id);
}
