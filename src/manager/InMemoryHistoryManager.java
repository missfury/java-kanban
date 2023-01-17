package manager;

import tasks.TaskTemplate;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    public static final Integer MAX_LIST_SIZE = 10;
    private static final List<TaskTemplate> historyList = new LinkedList<>();

    // Отметить задачу в листе просмотренных
    @Override
    public void historyAdd(TaskTemplate task) {
        if (task != null) {
            if (historyList.size() < MAX_LIST_SIZE) {
                historyList.add(task);
            } else {
                historyList.remove(0);
                historyList.add(task);
            }
            System.out.println("\r\nЗадача просмотрена");
        }
        else {
            System.out.println("\r\nЗадачи с таким ID не существует");
        }
    }

    // Отображение последних 10 просмотренных пользователем задач
    @Override
    public List<TaskTemplate> getHistory() {
        return historyList;
    }

}
