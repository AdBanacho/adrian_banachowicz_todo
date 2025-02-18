package ch.cern.todo.tasks.dataModels;

import java.util.Arrays;

public enum TaskPriorityStatus {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    CRITICAL("Critical");

    private final String name;

    TaskPriorityStatus(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public static TaskPriorityStatus getByName(String name){
        return Arrays.stream(values())
                .filter(status -> status.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
