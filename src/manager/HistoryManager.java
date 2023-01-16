package manager;

import tasks.TaskTemplate;

import java.util.List;

public interface HistoryManager {

    // Отметить задачу в листе просмотренных
    void historyAdd(TaskTemplate task);

    // Отображение последних просмотренных пользователем задач
    List<TaskTemplate>  getHistory();
}
