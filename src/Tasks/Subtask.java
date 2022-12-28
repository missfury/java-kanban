package Tasks;

public class Subtask extends TaskTemplate {

    private final Integer idEpic;

    public Subtask(Integer id, String name, String description, TaskStatus status, Integer idEpic) {
        super(id, name, description, status);
        this.idEpic = idEpic;
    }

    public Integer getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "subtaskId=" + getId() +
                ", subtaskName='" + getName() + '\'' +
                ", subtaskDescription='" + getDescription() + '\'' +
                ", subtaskIdEpic=" + idEpic +
                ", subtaskStatus='" + getStatus() +
                "'}";
    }
}
