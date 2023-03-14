package tasks;

import java.time.Duration;

public class Task extends TaskTemplate {

    public Task(String name, String description, TaskStatus status) {
        super(0, name, description, status);
    }

    public Task(String name, String description) {
        super(0, name, description);
    }

    //для взаимодействия с id при изменении задачи
    public Task(Integer id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
    }
    public Task(Integer id, String name, String description) {
        super(id, name, description);
    }

    public TypeOfTask getType() {
        return super.getType();
    }


    @Override
    public String toString() {
        return "\r\nTask{" +
                "taskId=" + getId() +
                ", taskName='" + getName() + '\'' +
                ", taskDescription='" + getDescription() + "'" +
                ", taskStatus='" + getStatus() +
                ", taskStart='" + getStartTime() +
                ", taskDuration='" + getDuration().toMinutes() +
                "'}";
    }

    @Override
    public String toStringFromFile() {
        String result = super.toStringFromFile();
        return result;
    }
    @Override
    public void resetDuration() {
        duration = Duration.ZERO;
    }
    @Override
    public void resetStartTime() {
        startTime = null;
    }
}
