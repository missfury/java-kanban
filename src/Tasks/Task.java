package Tasks;

public class Task extends TaskTemplate {
    public Task(Integer id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + getId() +
                ", taskName='" + getName() + '\'' +
                ", taskDescription='" + getDescription() + "'" +
                ", taskStatus='" + getStatus() +
                "'}";
    }
}
