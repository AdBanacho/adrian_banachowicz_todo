package ch.cern.todo.tasks;

import ch.cern.todo.tasks.dataModels.Task;
import ch.cern.todo.tasks.dataModels.TaskKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, TaskKey> {

    String INFINITE_TIME = "CAST('9999-12-31 12:00:00' AS timestamp)";

    @Query("SELECT t FROM Task t WHERE t.processedTo = " + INFINITE_TIME)
    Page<Task> findAllByProcessedTo(Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.id = :id AND t.processedTo = " + INFINITE_TIME)
    Optional<Task> findByIdAndProcessedTo(String id);
}
