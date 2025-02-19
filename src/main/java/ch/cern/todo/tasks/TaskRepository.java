package ch.cern.todo.tasks;

import ch.cern.todo.tasks.dataModels.Task;
import ch.cern.todo.tasks.dataModels.TaskKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, TaskKey>, JpaSpecificationExecutor<Task> {

    String INFINITE_TIME = "t.processedTo = CAST('9999-12-31 12:00:00' AS timestamp)";
    String STATUS_NOT_DELETED = "t.status <> TaskStatus.DELETED";
    String ACTIVE_CATEGORIES = STATUS_NOT_DELETED + " AND " + INFINITE_TIME;

    @Query("SELECT t FROM Task t WHERE " + ACTIVE_CATEGORIES)
    Page<Task> findAllByProcessedTo(Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.id = :id AND " + ACTIVE_CATEGORIES)
    Optional<Task> findByIdAndProcessedTo(String id);
}
