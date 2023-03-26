package manager;


import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskTemplate;

import java.util.Collection;
import java.util.List;

public interface TaskManager {

    // Добавление задачи типа "одиночная задача"
    Integer addTask(Task task);


    // Добавление задачи типа "масштабная задача с подзадачами"
    Integer addEpic (Epic epic);


    // Добавление задачи типа "подзадача в составе масштабной задачи"
    Integer addSubtask(Subtask subtask);


    //Обновление одиночной задачи
    void updateTask(Task task);

    //Обновление масштабной задачи
    void updateEpic(Epic epic);

    //Обновление подзадачи
    void updateSubtask(Subtask subtask);

    //Удаление всех одиночных задач
    void clearTaskList();

    //Удаление всех масштабных задач
    void clearEpicList();

    //Удаление всех подзадач
    void clearSubtaskList();

    //Удаление задач разного типа по идентификатору
    void remove(Integer id);

    //Удаление подзадачи по ID
    void removeSubtask(Integer id);

    //Удаление масштабной задачи по ID
    void removeEpic(Integer id);

    //Получение списка подзадач, принадлежащих к масштабной задаче
    List<Subtask> getSubtasksByEpicId(Integer id);

    //Получение одиночной задачи по идентификатору
    Task getTaskByID(Integer id);

    //Получение масштабной задачи по идентификатору
    Epic getEpicByID(Integer id);

    //Получение подзадачи по идентификатору
    Subtask getSubtaskByID(Integer id);

    //Получение списка одиночных задач
    List<Task> getTaskList();

    //Получение списка масштабных задач
    List<Epic>getEpicList();

    //Получение списка подзадач
    List<Subtask>getSubtaskList();

    // Обновление списка идентификаторов подзадач внутри масштабной задачи
    List<Integer> updateSubtasksEpicInside(Epic epic);

    // Обновление статуса масштабной задачи в зависимости от статусов подзадач
    void updateStatusOfEpic(Integer epicId);

    // Получить список просмотренных
    List<TaskTemplate> history();

    public void updateEpicStartTimeAndDuration(Integer id);

    Collection<TaskTemplate> getPrioritizedTasks();
    Collection<TaskTemplate> getAllTasks();
    List<TaskTemplate> getHistory();




}
