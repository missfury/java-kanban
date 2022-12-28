package tasks;

public class Task extends TaskTemplate {

    public Task(String name, String description, TaskStatus status) {
        super(0, name, description, status);
    }
    public Task(Integer id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
    }
    public Task(Integer id, String name, String description) {
        super(id, name, description);
    }

    @Override
    public String toString() {
        return "\r\nTask{" +
                "taskId=" + getId() +
                ", taskName='" + getName() + '\'' +
                ", taskDescription='" + getDescription() + "'" +
                ", taskStatus='" + getStatus() +
                "'}";
    }
}
