import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import static tasks.TaskStatus.IN_PROGRESS;
import static tasks.TaskStatus.NEW;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();
        manager.getPrioritizedTasks();

        manager.addTask(new Task("Задача 1", "Описание задачи 1", NEW));
        manager.addTask(new Task("Задача 2", "Описание задачи 2", NEW));
        manager.addEpic(new Epic("Эпик 1", "Описание эпика 1", NEW));
        manager.addSubtask(new Subtask("Подзадача 1 эпика 1", "Описание подзадачи", IN_PROGRESS,
        3));
        manager.addSubtask(new Subtask("Подзадача 2 эпика 1", "Описание подзадачи", NEW, 3));
        manager.addSubtask(new Subtask("Подзадача 3 эпика 1", "Описание подзадачи", NEW, 3));
        manager.addEpic(new Epic("Эпик 2", "Описание эпика 2", NEW));
        manager.addTask(new Task("Задача 3", "Описание задачи 3", NEW));

        manager.getTaskByID(1);
        manager.getEpicByID(3);
        manager.getTaskByID(2);
        manager.getSubtaskByID(6);
        manager.getSubtaskByID(4);
        manager.getSubtaskByID(5);
        manager.getEpicByID(7);

        System.out.println("\r\nИстория просмотров");
        System.out.println(manager.history().toString().replaceAll("^\\[|\\]$", ""));
        System.out.println("\r\nСписок одиночных задач");
        System.out.println(manager.getTaskList().toString().replaceAll("^\\[|\\]$", ""));
        System.out.println("\r\nСписок масштабных задач");
        System.out.println(manager.getEpicList().toString().replaceAll("^\\[|\\]$", ""));
        System.out.println("\r\nСписок подзадач");
        System.out.println(manager.getSubtaskList().toString().replaceAll("^\\[|\\]$", ""));

        System.out.println("!!!!!");
        Task task4 = manager.getTaskByID(1);
        Epic task5 = manager.getEpicByID(3);
        Subtask task6 = manager.getSubtaskByID(5);
        System.out.println("\r\nСписок задач по приоритету");
        System.out.println(manager.getPrioritizedTasks().toString().replaceAll("^\\[|\\]$", ""));
        task5.setStartTime("08:00 10.03.2023");
        task5.setDuration(100);
        task4.setStartTime("08:01 10.03.2023");
        task4.setDuration(100);
        task6.setStartTime("08:00 10.03.2023");
        task6.setDuration(360);
        manager.getTaskByID(2).setStartTime("10:01 12.03.2023");
        manager.getTaskByID(2).setDuration(50);
        manager.getTaskByID(8).setStartTime("10:01 14.03.2023");
        manager.getTaskByID(8).setDuration(50);
        manager.updateEpic(task5);
        manager.updateSubtask(task6);
        manager.updateTask(manager.getTaskByID(8));
        manager.updateTask(task4);
        manager.updateTask(manager.getTaskByID(2));
        System.out.println("\r\nИстория просмотров");
        System.out.println(manager.history().toString().replaceAll("^\\[|\\]$", ""));
        System.out.println("\r\nСписок подзадач");
        System.out.println(manager.getSubtaskList().toString().replaceAll("^\\[|\\]$", ""));
        System.out.println("\r\nСписок масштабных задач");
        System.out.println(manager.getEpicList().toString().replaceAll("^\\[|\\]$", ""));
        System.out.println("\r\nСписок одиночных задач");
        System.out.println(manager.getTaskList().toString().replaceAll("^\\[|\\]$", ""));
        System.out.println("\r\nСписок задач по приоритету");
        System.out.println(manager.getPrioritizedTasks().toString().replaceAll("^\\[|\\]$", ""));

    }
}
