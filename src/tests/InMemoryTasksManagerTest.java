package tests;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tasks.TaskStatus.IN_PROGRESS;
import static tasks.TaskStatus.NEW;

public class InMemoryTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setup() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void getPrioritizedTasksTest() {

        taskManager.addTask(new Task("Задача 1", "Описание задачи 1", NEW));
        taskManager.addTask(new Task("Задача 2", "Описание задачи 2", NEW));
        taskManager.addEpic(new Epic("Эпик 1", "Описание эпика 1", NEW));
        taskManager.addSubtask(new Subtask("Подзадача 1 эпика 1", "Описание подзадачи", IN_PROGRESS,
                3));
        taskManager.addSubtask(new Subtask("Подзадача 2 эпика 1", "Описание подзадачи", NEW, 3));
        taskManager.addEpic(new Epic("Эпик 2", "Описание эпика 2", NEW));

        taskManager.getTaskByID(1)
                .setStartTime("07:00 19.01.2021");
        taskManager.getTaskByID(1)
                .setDuration(10);
        taskManager.getTaskByID(2)
                .setStartTime("07:10 19.01.2021");
        taskManager.getTaskByID(2)
                .setDuration(3);
        taskManager.getEpicByID(3)
                .setStartTime("07:30 19.01.2021");
        taskManager.getSubtaskByID(4)
                .setStartTime("07:15 19.01.2021");
        taskManager.getSubtaskByID(4)
                .setDuration(15);
        taskManager.getSubtaskByID(5)
                .setStartTime("11:00 19.01.2021");
        taskManager.getSubtaskByID(5)
                .setDuration(30);
        taskManager.getEpicByID(6)
                .setStartTime("10:00 19.01.2021");
        taskManager.getEpicByID(6)
                .setDuration(30);
        taskManager.updateEpic(taskManager.getEpicByID(3));



        List<TaskTemplate> taskList = List.copyOf(taskManager.getPrioritizedTasks());
        List<TaskTemplate> testList = List.of(
                taskManager.getTaskByID(1),
                taskManager.getTaskByID(2),
                taskManager.getEpicByID(3),
                taskManager.getSubtaskByID(4),
                taskManager.getEpicByID(6),
                taskManager.getSubtaskByID(5)

        );
        assertEquals(testList, taskList);
    }
}
