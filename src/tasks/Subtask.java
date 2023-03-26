package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private Integer idEpic;


    public Subtask(String name, String description, TaskStatus status, Integer idEpic) {
        super(0, name, description, status);
        this.idEpic = idEpic;
    }

    public Subtask(String name, String description, Integer idEpic) {
        super(0, name, description);
        this.idEpic = idEpic;
    }

    public Subtask(Integer id, String name, String description, TaskStatus status, Integer idEpic) {
        super(id, name, description, status);
        this.idEpic = idEpic;
    }

    public Subtask(String name, String description, TaskStatus status,
                Duration duration, LocalDateTime startTime, Integer idEpic ) {
        super(name, description,status,
                duration, startTime);
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
