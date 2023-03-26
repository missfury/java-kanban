package tests;

import api.servers.KVServer;
import manager.HTTPTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskTemplate;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTasksManagerTest extends TaskManagerTest<HTTPTaskManager> {

    protected api.servers.KVServer KVServer;

    @BeforeEach
    public void setup() throws IOException {
        KVServer = new KVServer();
        KVServer.start();
        taskManager = new HTTPTaskManager();
    }

    @AfterEach
    void serverStop() {
        KVServer.stop();
    }

    @Test
    void loadFromServer() {
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
        List<Task> allTasks = taskManager.getTaskList();
        List<Epic> allEpics = taskManager.getEpicList();
        List<Subtask> allSubTasks = taskManager.getSubtaskList();
        Collection<TaskTemplate> prioritizedTasks = taskManager.getPrioritizedTasks();

        HTTPTaskManager taskManagerFromKv = new HTTPTaskManager();
        taskManagerFromKv.load();

        Collection<TaskTemplate> prioritizedTasksFromKV = taskManagerFromKv.getPrioritizedTasks();
        List<Task> allTasksFromKV = taskManagerFromKv.getTaskList();
        List<Epic> allEpicsFromKV = taskManagerFromKv.getEpicList();
        List<Subtask> allSubTasksFromKV = taskManagerFromKv.getSubtaskList();

        assertEquals(prioritizedTasks, prioritizedTasksFromKV);
        assertEquals(allTasks, allTasksFromKV);
        assertEquals(allEpics, allEpicsFromKV);
        assertEquals(allSubTasks, allSubTasksFromKV);
    }

}
