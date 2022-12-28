import Manager.TaskManager;
import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;

import static Tasks.TaskStatus.*;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Integer firstTask = taskManager.addTask(new Task("Создать профиль в социальной сети Нельзяграм для " +
        "проекта NoName", "Загрузить логотип, заполнить поисковую строку и шапку профиля, " +
        "сделать обложки для highlights.", NEW));
        Integer secondTask = taskManager.addTask(new Task("Отчет за декабрь по проекту " +
        "R2D2", "Составить отчет по проекту, отправить заказчику не позднее 10 января ", NEW));
        Integer firstEpic = taskManager.addEpic(new Epic("Провести аудит аккаунта компании" +
        " SithAirlines", " Проанализировать аккаунта компании в соцсети Нельзяграм и" +
        " составить отчет."));
        Integer firstSubtaskFirstEpic = taskManager.addSubtask(new Subtask("Оценить, насколько правильно" +
        " заполнен профиль", "Проверить читаемость логотипа, релевантность имени аккаунта и " +
        "поисковой строки, информативность описания.", NEW, firstEpic));
        Integer secondSubtaskFirstEpic = taskManager.addSubtask(new Subtask("Оценить " +
        "визуал", "Оценить качество визуального оформления ленты и highlights, проверить на " +
        "соответствие трендам. ", NEW, firstEpic));
        Integer secondEpic = taskManager.addEpic(new Epic("Запустить таргетированную рекламу для проекта" +
        " SithAirlines", " Оценить бюджет и запустить рекламную кампанию." +
        " Составить отчет с рекомендациями по масштабированию."));
        Integer firstSubtaskSecondEpic = taskManager.addSubtask(new Subtask("Определить целевую " +
        "аудиторию", "Проанализировать аудиторию компании, сравнить с конкурентами, " +
        "составить портрет покупателя.", NEW, secondEpic));

        System.out.println("\r\nСписок одиночных задач");
        System.out.println(taskManager.getTaskList().toString().replaceAll("^\\[|\\]$", ""));
        System.out.println("\r\nСписок масштабных задач");
        System.out.println(taskManager.getEpicList().toString().replaceAll("^\\[|\\]$", ""));
        System.out.println("\r\nСписок подзадач");
        System.out.println(taskManager.getSubtaskList().toString().replaceAll("^\\[|\\]$", ""));

        taskManager.updateTask(new Task(firstTask, "Новая задача", "", DONE));
        taskManager.updateSubtask(new Subtask(firstSubtaskFirstEpic, "Оценить, насколько правильно заполнен" +
        " профиль", "Проверить читаемость логотипа, релевантность имени аккаунта и поисковой строки, " +
        "информативность описания.", IN_PROGRESS, firstEpic));
        taskManager.updateEpic(new Epic(secondEpic, "Запустить таргетированную рекламу для" +
        " проекта и составить отчет.", "", DONE));

        System.out.println("\r\nСписок одиночных задач");
        System.out.println(taskManager.getTaskList().toString().replaceAll("^\\[|\\]$", ""));
        System.out.println("\r\nСписок масштабных задач");
        System.out.println(taskManager.getEpicList().toString().replaceAll("^\\[|\\]$", ""));
        System.out.println("\r\nСписок подзадач");
        System.out.println(taskManager.getSubtaskList().toString().replaceAll("^\\[|\\]$", ""));

        taskManager.remove(firstTask);
        taskManager.remove(secondSubtaskFirstEpic);
        taskManager.remove(secondEpic);

        System.out.println("\r\nСписок одиночных задач");
        System.out.println(taskManager.getTaskList().toString().replaceAll("^\\[|\\]$", ""));
        System.out.println("\r\nСписок масштабных задач");
        System.out.println(taskManager.getEpicList().toString().replaceAll("^\\[|\\]$", ""));
        System.out.println("\r\nСписок подзадач");
        System.out.println(taskManager.getSubtaskList().toString().replaceAll("^\\[|\\]$", ""));

    }
}
