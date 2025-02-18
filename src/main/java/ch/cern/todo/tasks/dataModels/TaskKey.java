package ch.cern.todo.tasks.dataModels;

import java.sql.Timestamp;
import java.util.Objects;

public class TaskKey{

    private String id;
    private Timestamp processedTo;

    public TaskKey() {}

    public TaskKey(String id, Timestamp processedTo) {
        this.id = id;
        this.processedTo = processedTo;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Timestamp getProcessedTo() { return processedTo; }
    public void setProcessedTo(Timestamp processedTo) { this.processedTo = processedTo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskKey taskKey = (TaskKey) o;
        return Objects.equals(id, taskKey.id) && Objects.equals(processedTo, taskKey.processedTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, processedTo);
    }
}
