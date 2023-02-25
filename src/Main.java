import manager.FileBackedTasksManager;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.nio.file.Path;

import static tasks.TaskStatus.IN_PROGRESS;
import static tasks.TaskStatus.NEW;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getFileBackedTasksManager(Path.of("data/data.csv"));

        manager.addTask(new Task("Задача 1", "Описание задачи 1", NEW));
        manager.addTask(new Task("Задача 2", "Описание задачи 2", NEW));
        manager.addEpic(new Epic("Эпик 1", "Описание эпика 1", NEW));
        manager.addSubtask(new Subtask("Подзадача 1 эпика 1", "Описание подзадачи", IN_PROGRESS,
        3));
        manager.addSubtask(new Subtask("Подзадача 2 эпика 1", "Описание подзадачи", NEW, 3));
        manager.addSubtask(new Subtask("Подзадача 3 эпика 1", "Описание подзадачи", NEW, 3));
        manager.addEpic(new Epic("Эпик 2", "Описание эпика 2", NEW));

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



        TaskManager manager1 = FileBackedTasksManager.loadFromFile(Path.of("data/data.csv"));


        System.out.println("\r\nИстория просмотров");
        System.out.println(manager1.history().toString().replaceAll("^\\[|\\]$", ""));
        System.out.println("\r\nСписок одиночных задач");
        System.out.println(manager1.getTaskList().toString().replaceAll("^\\[|\\]$", ""));
        System.out.println("\r\nСписок масштабных задач");
        System.out.println(manager1.getEpicList().toString().replaceAll("^\\[|\\]$", ""));
        System.out.println("\r\nСписок подзадач");
        System.out.println(manager1.getSubtaskList().toString().replaceAll("^\\[|\\]$", ""));


    }
}
