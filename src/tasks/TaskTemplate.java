package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskTemplate {
    protected Integer id;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected TypeOfTask type;
    protected Duration duration;
    protected LocalDateTime startTime;

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    public TaskTemplate (String name, String description, TaskStatus status,
    Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        setType();
        this.duration = duration;
        this.startTime = startTime;
    }

    public TaskTemplate(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        setType();
        this.duration = Duration.ZERO;
    }

    public TaskTemplate(Integer id, String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
        setType();
        this.duration = Duration.ZERO;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task object = (Task) obj;
        return this.id.equals(object.id)
                && this.name.equals(object.name)
                && this.description.equals(object.description)
                && this.status.equals(object.status)
                && this.type.equals(object.type);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatus status) {

        this.status = status;
    }

    public TaskStatus getStatus() {

        return status;
    }

    private void setType() {
        for (TypeOfTask type : TypeOfTask.values()) {
            if (this.getClass().equals(type.getType())) {
                this.type = type;
            }
        }
    }

    public TypeOfTask getType() {
        return type;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setDuration(long minutes) {
        this.duration = Duration.ofMinutes(minutes);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime setStartTime(String startTime) {
        this.startTime = LocalDateTime.parse(startTime, FORMATTER);
        return null;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null) {
            return startTime.plus(duration);
        }
        return null;
    }

    public void resetDuration() {
        duration = Duration.ZERO;
    }

    public void resetStartTime() {
        startTime = null;
    }

    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s", id, type, name, status, description, "");
    }


}
