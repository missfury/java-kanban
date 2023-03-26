package manager;

import java.nio.file.Path;

public class Managers {

    public static TaskManager getDefault(){
        return new HTTPTaskManager();
    }

    public static TaskManager getFileBackedTasksManager(Path path) {
        return new FileBackedTasksManager(path);
    }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
