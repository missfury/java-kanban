package tasks;

public enum TypeOfTask{
    TASK("Обычная", Task.class),
    SUBTASK("Подзадача", Subtask.class),
    EPIC("Эпик", Epic.class);

    private final String name;
    private final Class<? extends TaskTemplate> type;

    TypeOfTask (String name, Class<? extends TaskTemplate> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<? extends TaskTemplate> getType() {
        return type;
    }
}


