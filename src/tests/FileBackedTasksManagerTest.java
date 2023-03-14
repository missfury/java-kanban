package tests;

import manager.FileBackedTasksManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskTemplate;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @BeforeEach
    void setup() {
        taskManager = new FileBackedTasksManager();
    }

    @Test
    void loadedFromFileTasksManager() {
        final Path filePath = Paths.get("./data/data.csv");
        final File file = new File(String.valueOf(filePath));
        createTask();
        Task task = createTask();
        Epic epic1 = createEpic();
        Epic epic2 = createEpic();
        createSubtask(epic1);
        createSubtask(epic1);
        createSubtask(epic2);
        Subtask subtask = createSubtask(epic2);
        taskManager.getTaskByID(task.getId());
        taskManager.getSubtaskByID(subtask.getId());
        taskManager.getEpicByID(epic1.getId());
        List<TaskTemplate> history = taskManager.history();

        FileBackedTasksManager taskManagerFromFile = FileBackedTasksManager.loadFromFile(file.toPath());
        List<TaskTemplate> historyFromFile = taskManagerFromFile.history();
        List<Task> allTasksFromFile = taskManagerFromFile.getTaskList();
        List<Epic> allEpicsFromFile = taskManagerFromFile.getEpicList();
        List<Subtask> allSubTasksFromFile = taskManagerFromFile.getSubtaskList();
        List<Task> allTasks = taskManager.getTaskList();
        List<Epic> allEpics = taskManager.getEpicList();
        List<Subtask> allSubTasks = taskManager.getSubtaskList();

        assertEquals(history, historyFromFile);
        assertEquals(allTasks, allTasksFromFile);
        assertEquals(allEpics, allEpicsFromFile);
        assertEquals(allSubTasks, allSubTasksFromFile);
    }

}
