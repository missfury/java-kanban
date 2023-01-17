import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskTemplate;

import java.util.List;

import static tasks.TaskStatus.*;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();
        List<TaskTemplate> getHistory = Managers.getDefaultHistory();


        taskManager.addTask(new Task("Создать профиль в социальной сети Нельзяграм для " +
        "проекта NoName", "Загрузить логотип, заполнить поисковую строку и шапку профиля, " +
        "сделать обложки для highlights.", NEW));
        taskManager.addTask(new Task("Отчет за декабрь по проекту " +
        "R2D2", "Составить отчет по проекту, отправить заказчику не позднее 10 января ", NEW));
        taskManager.addEpic(new Epic("Провести аудит аккаунта компании" +
        " SithAirlines", " Проанализировать аккаунта компании в соцсети Нельзяграм и" +
        " составить отчет."));
        taskManager.addSubtask(new Subtask("Оценить, насколько правильно" +
        " заполнен профиль", "Проверить читаемость логотипа, релевантность имени аккаунта и " +
        "поисковой строки, информативность описания.", NEW, 3));
        taskManager.addSubtask(new Subtask("Оценить " +
        "визуал", "Оценить качество визуального оформления ленты и highlights, проверить на " +
        "соответствие трендам. ", NEW, 3));
        taskManager.addEpic(new Epic("Запустить таргетированную рекламу для проекта" +
        " SithAirlines", " Оценить бюджет и запустить рекламную кампанию." +
        " Составить отчет с рекомендациями по масштабированию."));
        taskManager.addSubtask(new Subtask("Определить целевую " +
        "аудиторию", "Проанализировать аудиторию компании, сравнить с конкурентами, " +
        "составить портрет покупателя.", NEW, 6));

        System.out.println("\r\nСписок одиночных задач");
        System.out.println(taskManager.getTaskList().toString().replaceAll("^\\[|\\]$", ""));
        System.out.println("\r\nСписок масштабных задач");
        System.out.println(taskManager.getEpicList().toString().replaceAll("^\\[|\\]$", ""));
        System.out.println("\r\nСписок подзадач");
        System.out.println(taskManager.getSubtaskList().toString().replaceAll("^\\[|\\]$", ""));

        taskManager.updateTask(new Task(1, "Новая задача", "Исправлено описание", DONE));
        taskManager.updateSubtask(new Subtask(4, "Оценить, насколько правильно заполнен" +
        " профиль", "И здесь исправлено описание.", IN_PROGRESS, 3));
        taskManager.updateEpic(new Epic(6, "Запустить таргетированную рекламу для" +
        " проекта и составить отчет.", "Выходит, что описание исправить можно. Но я все же добавила " +
                "сеттер для описания в TaskTemplate, хотя он нигде не используется.", DONE));
        taskManager.updateSubtask(new Subtask(7, "Новое название", "Новое описание", IN_PROGRESS, 6));

        System.out.println("\r\nСписок одиночных задач");
        System.out.println(taskManager.getTaskList().toString().replaceAll("^\\[|\\]$", ""));
        System.out.println("\r\nСписок масштабных задач");
        System.out.println(taskManager.getEpicList().toString().replaceAll("^\\[|\\]$", ""));
        System.out.println("\r\nСписок подзадач");
        System.out.println(taskManager.getSubtaskList().toString().replaceAll("^\\[|\\]$", ""));

        taskManager.remove(1);
        taskManager.remove(5);
        taskManager.remove(3);

        taskManager.addTask(new Task("Еще одна новая задача", "", NEW));

        System.out.println("\r\nСписок одиночных задач");
        System.out.println(taskManager.getTaskList().toString().replaceAll("^\\[|\\]$", ""));
        System.out.println("\r\nСписок масштабных задач");
        System.out.println(taskManager.getEpicList().toString().replaceAll("^\\[|\\]$", ""));
        System.out.println("\r\nСписок подзадач");
        System.out.println(taskManager.getSubtaskList().toString().replaceAll("^\\[|\\]$", ""));

        taskManager.getSubtaskByID(7);
        taskManager.getEpicByID(6);
        taskManager.getTaskByID(2);
        taskManager.getTaskByID(8);

        System.out.println("\r\nИстория просмотров");
        System.out.println(getHistory.toString().replaceAll("^\\[|\\]$", ""));

        taskManager.addTask(new Task("Задача 5", "Описание задачи 5", NEW));
        taskManager.addTask(new Task("Задача 6", "Описание задачи 6", NEW));
        taskManager.addTask(new Task("Задача 7", "Описание задачи 7", NEW));
        taskManager.addTask(new Task("Задача 8", "Описание задачи 8", NEW));
        taskManager.addTask(new Task("Задача 9", "Описание задачи 9", NEW));
        taskManager.addTask(new Task("Задача 10", "Описание задачи 10", NEW));
        taskManager.addTask(new Task("Задача 11", "Описание задачи 11", NEW));

        taskManager.getTaskByID(9);
        taskManager.getTaskByID(10);
        taskManager.getTaskByID(11);
        taskManager.getTaskByID(12);
        taskManager.getTaskByID(13);
        taskManager.getTaskByID(14);
        taskManager.getTaskByID(15);
        taskManager.getTaskByID(1500);

        System.out.println("\r\nИстория просмотров");
        System.out.println(getHistory.toString().replaceAll("^\\[|\\]$", ""));

    }
}
