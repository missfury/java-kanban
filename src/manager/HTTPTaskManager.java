package manager;

import api.adapters.DurationAdapter;
import api.adapters.LocalDateTimeAdapter;
import api.client.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskTemplate;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class HTTPTaskManager extends FileBackedTasksManager {
    private static Gson gson;
    private final KVTaskClient kvTaskClient;

    public HTTPTaskManager() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        kvTaskClient = new KVTaskClient();
    }

    @Override
    public void save() {
        kvTaskClient.put("history", gson.toJson(getHistory()));
        kvTaskClient.put("tasks", gson.toJson(tasks));
        kvTaskClient.put("epics", gson.toJson(epics));
        kvTaskClient.put("subtasks", gson.toJson(subtasks));
    }

    public void load() {
        String jsonTasks = kvTaskClient.load("tasks");
        Type tasksType = new TypeToken<HashMap<Integer, Task>>(){}.getType();
        HashMap<Integer, Task> loadedTasks = gson.fromJson(jsonTasks, tasksType);
        for (Task task : loadedTasks.values()){
            super.loadTask(task);
        }

        String jsonEpics = kvTaskClient.load("epics");
        Type epicsType = new TypeToken<HashMap<Integer, Epic>>(){}.getType();
        HashMap<Integer, Epic> loadedEpics = gson.fromJson(jsonEpics, epicsType);
        for (Epic epic : loadedEpics.values()){
            epic.clearSubtaskList();
            super.loadEpic(epic);
        }

        String jsonSubtasks = kvTaskClient.load("subtasks");
        Type subtasksType = new TypeToken<HashMap<Integer, Subtask>>(){}.getType();
        HashMap<Integer, Subtask> loadedSubtasks = gson.fromJson(jsonSubtasks, subtasksType);
        for (Subtask subtask : loadedSubtasks.values()){
            super.loadSubtask(subtask);
        }

        String jsonHistory = kvTaskClient.load("history");
        Type historyType = new TypeToken<List<TaskTemplate>>(){}.getType();
        List<TaskTemplate> history = gson.fromJson(jsonHistory, historyType);
        for (TaskTemplate task: history){
            historyManager.add(findTask(task.getId()));
        }
    }
}
