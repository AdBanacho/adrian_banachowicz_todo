package ch.cern.todo.category.dataModels;

import java.sql.Timestamp;
import java.util.Objects;

public class CategoryKey {

    private String id;
    private Timestamp processedTo;

    public CategoryKey() {}

    public CategoryKey(String id, Timestamp processedTo) {
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
        CategoryKey categoryKey = (CategoryKey) o;
        return Objects.equals(id, categoryKey.id) && Objects.equals(processedTo, categoryKey.processedTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, processedTo);
    }
}
