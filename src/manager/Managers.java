package manager;

import tasks.TaskTemplate;

import java.util.List;

public class Managers {

    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }

    public static List<TaskTemplate> getDefaultHistory() {
        return new InMemoryHistoryManager().getHistory();
    }
}
