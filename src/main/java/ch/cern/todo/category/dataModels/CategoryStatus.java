package ch.cern.todo.category.dataModels;


import java.util.Arrays;

public enum CategoryStatus {
    ACTIVE("Active"),
    DELETED("Deleted");

    private final String name;

    CategoryStatus(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public static CategoryStatus getByName(String name){
        return Arrays.stream(values())
                .filter(status -> status.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public boolean isDeleted(){
        return CategoryStatus.DELETED.equals(this);
    }
}
