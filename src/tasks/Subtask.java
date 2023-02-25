package tasks;

public class Subtask extends Task {

    private final Integer idEpic;


    public Subtask(String name, String description, TaskStatus status, Integer idEpic) {
        super(0, name, description, status);
        this.idEpic = idEpic;
    }

    public Subtask(Integer id, String name, String description, TaskStatus status, Integer idEpic) {
        super(id, name, description, status);
        this.idEpic = idEpic;
    }

    public Integer getIdEpic() {
        return idEpic;
    }



    @Override
    public String toString() {
        String result = super.toString().replaceAll("\\}$", "");
        return result + ", IdEpic=" + idEpic + '}';

    }

    @Override
    public String toStringFromFile() {
        String result = super.toStringFromFile();
        return result + idEpic;
    }
}
