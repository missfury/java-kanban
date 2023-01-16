package manager;

import tasks.TaskTemplate;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    public static final Integer MAX_LIST_SIZE = 10;
    private static final List<TaskTemplate> historyList = new ArrayList<>();

    // Отметить задачу в листе просмотренных
    @Override
    public void historyAdd(TaskTemplate task) {
        if (historyList.size() < MAX_LIST_SIZE) {
            historyList.add(task);
        } else {
            historyList.remove(0);
            historyList.add(task);
        }
    }

    // Отображение последних 10 просмотренных пользователем задач
    @Override
    public List<TaskTemplate> getHistory() {
        return historyList;
    }

}
