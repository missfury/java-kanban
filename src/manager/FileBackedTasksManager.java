package manager;

import exceptions.ManagerSaveException;
import tasks.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private Path fileName;
    private static final Path DEFAULT_FILE = Paths.get("./data/data.csv");
    private static final String SEPARATOR = ",";
    private static final String HEAD = "id,type,name,status,description,epicID";
    private static final String NEW_LINE = "\n";
    protected HistoryManager historyManager = Managers.getDefaultHistory();

    public Path getTasksFile() {
        return fileName;
    }

    public void setTasksFile(Path tasksFile) {
        this.fileName = tasksFile;
    }

    public FileBackedTasksManager(Path tasksFile) {
        this.fileName = tasksFile;
    }

    public FileBackedTasksManager() {

        this.fileName = DEFAULT_FILE;
    }


    public String getRealPath(Path path) {
        String filePath = null;
        try {
            filePath = path.toRealPath().toString();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return filePath;
    }

    private String toString(TaskTemplate task) {
        String result = task.getId() + SEPARATOR + task.getType() + SEPARATOR
                + task.getName() + SEPARATOR + task.getStatus() + SEPARATOR
                + task.getDescription();
        if (task.getType().equals(TypeOfTask.SUBTASK)) {
            Subtask subtask = (Subtask) task;
            result += SEPARATOR + subtask.getIdEpic();
        } else if (task.getType().equals(TypeOfTask.EPIC)) {
            Epic epic = (Epic) task;
            for (Integer id : epic.getSubtaskList()) {
                result += SEPARATOR + id;
            }
        }
        return result;
    }

    protected String historyToString (HistoryManager manager) {
        String result = "";
        if (manager != null) {
            List<TaskTemplate> historyList = manager.getHistory();
            if (historyList != null) {
                for (TaskTemplate task : historyList) {
                    result += task.getId() + SEPARATOR;
                }
                result = result.substring(0, result.length() );
            }
        }
        return result;
    }


    public void save() {
        String filePath = getRealPath(fileName);
        Collection<Task> taskList = super.getTaskList();
        Collection<Epic> epicList = super.getEpicList();
        Collection<Subtask> subtaskList = super.getSubtaskList();

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            if (!Files.exists(fileName)) {
                Files.createFile(fileName);
            }
            fileWriter.write(HEAD + NEW_LINE);
            if (taskList != null || epicList != null || subtaskList != null) {
                for (Task task : taskList) {
                    fileWriter.write(toString(task) + NEW_LINE);
                }
                for (Epic epic : epicList) {
                    fileWriter.write(toString(epic) + NEW_LINE);
                }
                for (Subtask subtask : subtaskList) {
                    fileWriter.write(toString(subtask) + NEW_LINE);
                }
                fileWriter.write(NEW_LINE);
                String history = historyToString(historyManager);
                if (history != null) {
                    fileWriter.write(history);
                }
            }
        } catch (IOException ioException) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла.");
        }
    }

    private static TaskTemplate fromString(String value) {
        int arrayIndexForEpicsOrSubtasks = 5;
        TaskTemplate task;

        String[] values = value.split(SEPARATOR);
        try {
            Integer id = Integer.parseInt(values[0]);
            TypeOfTask type = TypeOfTask.valueOf(values[1]);
            String name = values[2];
            TaskStatus status = TaskStatus.valueOf(values[3]);
            String description = values[4];

            if (type.equals(TypeOfTask.SUBTASK)) {
                Integer idEpic = Integer.parseInt(values[5]);
                task = new Subtask(name, description, status, idEpic);
            } else if (type.equals(TypeOfTask.EPIC)) {
                List<Integer> subtaskList = new ArrayList<>();
                for (int i = arrayIndexForEpicsOrSubtasks; i < values.length; i++ ) {
                    Integer subtask = Integer.parseInt(values[i]);
                    subtaskList.add(subtask);
                }
                task = new Epic(name, description, status, subtaskList);
            } else {
                task = new Task(name, description, status);
            }
            task.setId(id);
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            throw new ManagerSaveException("Произошла ошибка во время чтения файла.");
        }
        return task;
    }

    protected static List<Integer> historyFromString(String value) {
        String[] tasksId = value.split(SEPARATOR);
        List<Integer> result = new ArrayList<>();
        for (String stringId : tasksId) {
            Integer id = Integer.parseInt(stringId);
            result.add(id);
        }
        return result;
    }

    public static FileBackedTasksManager loadFromFile(Path file) {
        int idCounter = 0;
        if (!Files.exists(file)) {
            return new FileBackedTasksManager();
        }
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);
        String filePath = fileManager.getRealPath(file);
        try (BufferedReader reader = new BufferedReader (new FileReader(filePath))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (!line.isEmpty()) {
                    if (line.equals(HEAD)) {
                        continue;
                    }
                    TaskTemplate task = fromString(line);
                    if (task.getType().equals(TypeOfTask.TASK)) {
                        fileManager.tasks.put(task.getId(), (Task) task);
                    }
                    else if (task.getType().equals(TypeOfTask.EPIC)) {
                        fileManager.epics.put(task.getId(), (Epic) task);
                    }
                    else if (task.getType().equals(TypeOfTask.SUBTASK)) {
                        fileManager.subtasks.put(task.getId(), (Subtask) task);
                    }
                    Integer intermediate = task.getId();
                    if (idCounter < intermediate) {
                        idCounter = intermediate;
                    }

                } else {
                    line = reader.readLine();
                    if (line == null) {
                        fileManager.historyManager = null;
                    } else {
                        ArrayList<Integer> historyIdTasks = (ArrayList<Integer>) historyFromString(line);
                        for (int id : historyIdTasks) {
                            Task task = fileManager.tasks.get(id);
                            Epic epic = fileManager.epics.get(id);
                            Subtask subtask = fileManager.subtasks.get(id);
                            fileManager.historyManager.historyAdd(task);
                            fileManager.historyManager.historyAdd(epic);
                            fileManager.historyManager.historyAdd(subtask);
                        }
                    }
                }
            }

        } catch (IOException ioException) {
            throw new ManagerSaveException("Произошла ошибка во время чтения файла.");
        }
        return fileManager;
    }



    @Override
    public Integer addTask(Task task) {
        Integer taskId = super.addTask(task);
        this.save();
        return taskId;
    }

    @Override
    public Integer addEpic(Epic epic) {
        Integer epicId = super.addEpic(epic);
        this.save();
        return epicId;
    }

    @Override
    public Integer addSubtask(Subtask subtask) {
        Integer subtaskId = super.addSubtask(subtask);
        this.save();
        return subtaskId;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void clearTaskList() {
        super.clearTaskList();
        save();
    }

    @Override
    public void clearEpicList() {
        super.clearEpicList();
        save();
    }

    @Override
    public void clearSubtaskList() {
        super.clearSubtaskList();
        save();
    }

    @Override
    public void remove(Integer id) {
        super.remove(id);
        save();
    }

    @Override
    public void removeSubtask(Integer id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void removeEpic(Integer id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(Integer id) {
        List<Subtask> list = super.getSubtasksByEpicId(id);
        save();
        return list;
    }

    @Override
    public List<Task> getTaskList() {
        List<Task> list = super.getTaskList();
        return list;
    }

    @Override
    public List<Epic>getEpicList() {
        List<Epic> list = super.getEpicList();
        return list;
    }

    @Override
    public List<Subtask>getSubtaskList() {
        List<Subtask> list = super.getSubtaskList();
        return list;
    }

    @Override
    public Task getTaskByID(Integer id) {
        Task task = super.getTaskByID(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicByID(Integer id) {
        Epic epic = super.getEpicByID(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskByID(Integer id) {
        Subtask subtask = super.getSubtaskByID(id);
        save();
        return subtask;
    }

    @Override
    public List<TaskTemplate> history() {
        List<TaskTemplate> list = super.history();
        return list;
    }







}
