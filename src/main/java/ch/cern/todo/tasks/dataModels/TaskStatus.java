package ch.cern.todo.tasks.dataModels;

import java.util.Arrays;

public enum TaskStatus {
    CREATED("Created"),
    IN_PROGRESS("In Progress"),
    BLOCKED("Blocked"),
    HOLD("Hold"),
    INACTIVE("Inactive"),
    DELETED("Deleted"),
    COMPLETED("Completed"),
    CLOSED("Closed");

    private final String name;

    TaskStatus(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public static TaskStatus getByName(String name){
        return Arrays.stream(values())
                .filter(status -> status.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
