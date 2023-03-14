package tests;

import manager.TaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TaskManagerTest <T extends TaskManager> {

    protected T taskManager;

    protected Task createTask() {
        int taskId = taskManager.addTask(new Task("Title", "Description"));
        return taskManager.getTaskByID(taskId);
    }

    protected Epic createEpic() {
        int epicId = taskManager.addEpic(new Epic("Title", "Description"));
        return taskManager.getEpicByID(epicId);
    }

    protected Subtask createSubtask(Epic epic) {
        int subtaskId = taskManager.addSubtask(new Subtask("Title", "Description", epic.getId()));
        return taskManager.getSubtaskByID(subtaskId);
    }

    @Test
    public void addTask() {
        Task task = createTask();
        List<Task> tasks = taskManager.getTaskList();
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(List.of(task), tasks);
    }

    @Test
    public void addEpic() {
        Epic epic = createEpic();
        List<Epic> epics = taskManager.getEpicList();
        assertEquals(TaskStatus.NEW, epic.getStatus());
        assertEquals(Collections.EMPTY_LIST, epic.getSubtaskList());
        assertEquals(List.of(epic), epics);
    }

    @Test
    public void addSubtask() {
        Epic epic = createEpic();
        Subtask subtask = createSubtask(epic);
        List<Subtask> subtasks = taskManager.getSubtaskList();

        assertEquals(epic.getId(), subtask.getIdEpic());
        assertEquals(TaskStatus.NEW, epic.getStatus());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
        assertEquals(List.of(subtask), subtasks);
        assertEquals(List.of(subtask.getId()), epic.getSubtaskList());
    }

    @Test
    public void updateTaskStatus() {
        Task task = createTask();
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTaskByID(task.getId()).getStatus());
    }
    @ParameterizedTest
    @EnumSource(TaskStatus.class)
    public void updateSubtaskStatusWithAllStatuses(TaskStatus status) {
        Epic epic = createEpic();
        Subtask subtask = createSubtask(epic);
        subtask.setStatus(status);
        taskManager.updateSubtask(subtask);

        assertEquals(status, taskManager.getSubtaskByID(subtask.getId()).getStatus());
        assertEquals(status, taskManager.getEpicByID(epic.getId()).getStatus());
    }

    @Test
    public void updateEpicStatusToInProgress() {
        Epic epic = createEpic();
        Subtask subtask1 = createSubtask(epic);
        Subtask subtask2 = createSubtask(epic);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicByID(epic.getId()).getStatus());
    }

    @Test
    public void updateEpicStatusToDone() {
        Epic epic = createEpic();
        Subtask subtask1 = createSubtask(epic);
        Subtask subtask2 = createSubtask(epic);
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        assertEquals(TaskStatus.DONE, taskManager.getEpicByID(epic.getId()).getStatus());
    }

    @Test
    public void removeAllTasks() {
        createTask();
        Epic epic = createEpic();
        createSubtask(epic);
        taskManager.clearTaskList();
        taskManager.clearSubtaskList();
        taskManager.clearEpicList();

        assertEquals(Collections.EMPTY_LIST, taskManager.getTaskList());
        assertEquals(Collections.EMPTY_LIST, taskManager.getEpicList());
        assertEquals(Collections.EMPTY_LIST, taskManager.getSubtaskList());
    }

    @Test
    public void calculateStartAndEndTime() {
        Epic epic = createEpic();
        Subtask subtask = createSubtask(epic);
        subtask.setStartTime(LocalDateTime.now());
        subtask.setDuration(360);
        taskManager.updateEpic(epic);
        assertEquals(subtask.getStartTime(), epic.getStartTime());
        assertEquals(subtask.getEndTime(), epic.getEndTime());
    }

    @Test
    public void removeTask() {
        Task task = createTask();
        taskManager.remove(task.getId());

        assertEquals(Collections.EMPTY_LIST, taskManager.getTaskList());
    }

    @Test
    public void removeEpic() {
        Epic epic = createEpic();
        createSubtask(epic);
        createSubtask(epic);
        taskManager.remove(epic.getId());
        assertEquals(Collections.EMPTY_LIST, taskManager.getEpicList());
        assertEquals(Collections.EMPTY_LIST, taskManager.getSubtaskList());
    }

    @Test
    public void removeSubtask() {
        Epic epic = createEpic();
        Subtask subtask = createSubtask(epic);
        taskManager.remove(subtask.getId());
        assertEquals(Collections.EMPTY_LIST, taskManager.getSubtaskList());
        assertEquals(Collections.EMPTY_LIST, taskManager.getEpicByID(epic.getId()).getSubtaskList());
    }

    @Test
    public void returnSubtasksByEpicId() {
        Epic epic = createEpic();
        Subtask subtask1 = createSubtask(epic);
        Subtask subtask2 = createSubtask(epic);
        Subtask subtask3 = createSubtask(epic);

        assertEquals(List.of(subtask1, subtask2, subtask3), taskManager.getSubtasksByEpicId(epic.getId()));
    }

    @Test
    public void returnEmptyHistory() {
        assertEquals(Collections.EMPTY_LIST, taskManager.history());
    }

    @Test
    public void returnHistory() {
        Task task = createTask();
        Epic epic = createEpic();
        Subtask subtask = createSubtask(epic);
        taskManager.getTaskByID(task.getId());
        taskManager.getEpicByID(epic.getId());
        taskManager.getSubtaskByID(subtask.getId());

        assertEquals(List.of(task, epic, subtask), taskManager.history());
    }












}
