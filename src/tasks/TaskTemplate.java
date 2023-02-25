package tasks;

public class TaskTemplate {
    protected Integer id;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected TypeOfTask type;

    public TaskTemplate(Integer id, String name, String description, TaskStatus status, TypeOfTask type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        setType();
    }

    public TaskTemplate(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        setType();
    }

    public TaskTemplate(Integer id, String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
        setType();
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

    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s", id, type, name, status, description, "");
    }


}
