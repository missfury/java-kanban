package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> subtaskList;

    public Epic(String name, String description, TaskStatus status ) {
        super(0, name, description);
        subtaskList = new ArrayList<>();
    }

    public Epic(String name, String description, TaskStatus status, List<Integer> subtaskList ) {
        super(0, name, description);
        this.status = status;
        this.subtaskList = new ArrayList<>();
    }




    public List<Integer> getSubtaskList() {
        return subtaskList;
    }

    public void setSubtaskList(List<Integer> subtaskList) {
        this.subtaskList = subtaskList;
    }

    @Override
    public String toString() {
        String result = super.toString().replaceAll("\\}$", "");
        return result + ", SubtaskList=" + subtaskList + '}';

    }

    @Override
    public String toStringFromFile() {
        String result = super.toStringFromFile();
        return result;
    }
    @Override
    public TypeOfTask getType() {
        return super.getType();
    }
}
