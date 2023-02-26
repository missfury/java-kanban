package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static tasks.TaskStatus.*;

public class InMemoryTaskManager implements TaskManager {

    protected int id;
    protected static Map<Integer, Task> tasks;
    protected static Map<Integer, Subtask> subtasks;
    protected static Map<Integer, Epic> epics;
    protected Integer nextId;
    protected static HistoryManager historyManager;

    public InMemoryTaskManager() {
        id = 0;
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.nextId = 1;
        historyManager = Managers.getDefaultHistory();
    }

    // Добавление задачи типа "одиночная задача"
    @Override
    public Integer addTask(Task task) {
        setNextId(nextId);
        task.setId(nextId);
        tasks.put(task.getId(), task);
        return nextId++;
    }

    // Добавление задачи типа "масштабная задача с подзадачами"
    @Override
    public Integer addEpic (Epic epic) {
        setNextId(nextId);
        epic.setId(nextId++);
        epic.setStatus(NEW);
        List<Integer> epicList = updateSubtasksEpicInside(epic);
        epic.setSubtaskList(epicList);
        epics.put(epic.getId(), epic);
        return epic.getId();

    }

    // Добавление задачи типа "подзадача в составе масштабной задачи"
    @Override
    public Integer addSubtask(Subtask subtask) {
        setNextId(nextId);
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        List<Integer> subtaskList = updateSubtasksEpicInside(epics.get(subtask.getIdEpic()));
        Epic epic = epics.get(subtask.getIdEpic());
        subtaskList.add(subtask.getId());
        epic.setSubtaskList(subtaskList);
        epics.put(subtask.getIdEpic(), epic);
        return subtask.getId();
    }

    // Обновление списка идентификаторов подзадач внутри масштабной задачи
    @Override
    public List<Integer> updateSubtasksEpicInside(Epic epic) {
        List<Integer> subtasksId = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskList()) {
            Subtask subtask = subtasks.get(subtaskId);
            subtasksId.add(subtask.getId());
        }
        epic.setSubtaskList(subtasksId);

        return epic.getSubtaskList();
    }

    // Обновление статуса масштабной задачи в зависимости от статусов подзадач
    @Override
    public void updateStatusOfEpic(Integer epicId) {

        Integer countNewStatus = 0;
        Integer countDoneStatus = 0;
        Integer countSubtaskList = epics.get(epicId).getSubtaskList().size();

        for (Integer subtaskId : epics.get(epicId).getSubtaskList()) {
            if (subtasks.get(subtaskId).getStatus() == NEW) {
                countNewStatus++;
            } else if (subtasks.get(subtaskId).getStatus() == DONE) {
                countDoneStatus++;
            }
        }

        if (countNewStatus.equals(countSubtaskList) || (countSubtaskList == 0)) {
            Epic epic = epics.get(epicId);
            epic.setStatus(NEW);
            epics.put(epicId, epic);
        } else if (countDoneStatus.equals(countSubtaskList)) {
            Epic epic = epics.get(epicId);
            epic.setStatus(DONE);
            epics.put(epicId, epic);
        } else {
            Epic epic = epics.get(epicId);
            epic.setStatus(IN_PROGRESS);
            epics.put(epicId, epic);
        }
    }

    //Обновление одиночной задачи
    @Override
    public void updateTask(Task task) {
        int id = task.getId();
        tasks.put(id, task);
        historyManager.updateId(tasks.get(id));
    }

    //Обновление масштабной задачи
    @Override
    public void updateEpic(Epic epic) {
        epic.setSubtaskList(epics.get(epic.getId()).getSubtaskList());
        epics.put(epic.getId(), epic);
        historyManager.updateId(tasks.get(epic.getId()));
        updateStatusOfEpic(epic.getId());
    }

    //Обновление подзадачи
    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        historyManager.updateId(tasks.get(subtask.getId()));
        Epic epic = epics.get(subtask.getIdEpic());
        updateSubtasksEpicInside(epic);
        updateStatusOfEpic(subtask.getIdEpic());
    }

    //Удаление всех одиночных задач
    @Override
    public void clearTaskList() {
        tasks.clear();
    }

    //Удаление всех масштабных задач
    @Override
    public void clearEpicList() {
        epics.clear();
        subtasks.clear();
    }

    //Удаление всех подзадач
    @Override
    public void clearSubtaskList() {
        subtasks.clear();
    }

    //Удаление подзадачи по ID
    @Override
    public void removeSubtask(Integer id) {
        subtasks.remove(id);
        historyManager.remove(id);
    }

    //Удаление масштабной задачи по ID
    @Override
    public void removeEpic(Integer id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            epics.remove(id);
            historyManager.remove(id);
            for (Integer subtaskId : epic.getSubtaskList()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            epic.setSubtaskList(new ArrayList<>());
        }

    }

    //Удаление задач разного типа по идентификатору
    @Override
    public void remove(Integer id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);
        } else if (subtasks.containsKey(id)) {
            removeSubtask(id);

        } else if (epics.containsKey(id)) {
            removeEpic(id);

        } else {
            System.out.println("ID не существует");
        }
    }

    //Получение списка подзадач, принадлежащих к масштабной задаче
    @Override
    public List<Subtask> getSubtasksByEpicId(Integer id) {
        List<Subtask> list = new ArrayList<>();
        for (Integer subtask : epics.get(id).getSubtaskList()) {
            list.add(subtasks.get(subtask));
        }
        return list;
    }

    //Получение одиночной задачи по идентификатору
    @Override
    public Task getTaskByID(Integer id) {
        setNextId(nextId);
        Task task = tasks.getOrDefault(id, null);
        historyManager.historyAdd(task);
        return task;
    }

    //Получение масштабной задачи по идентификатору
    @Override
    public Epic getEpicByID(Integer id) {
        setNextId(nextId);
        Epic epic = epics.getOrDefault(id, null);
        historyManager.historyAdd(epic);
        return epic;
    }

    //Получение подзадачи по идентификатору
    @Override
    public Subtask getSubtaskByID(Integer id) {
        setNextId(nextId);
        historyManager.historyAdd(subtasks.get(id));

        return subtasks.get(id);
    }

    //Получение списка одиночных задач
    @Override
    public List<Task> getTaskList() {
        List<Task> list = new ArrayList<>();
        for (Integer task : tasks.keySet()) {
            list.add(tasks.get(task));
        }
        return list;
    }

    //Получение списка масштабных задач
    @Override
    public List<Epic>getEpicList() {
        List<Epic> list = new ArrayList<>();
        for (Integer epic : epics.keySet()) {
            list.add(epics.get(epic));
        }
        return list;
    }

    // Получение списка подзадач
    @Override
    public List<Subtask>getSubtaskList() {
        List<Subtask> list = new ArrayList<>();
        for (Integer subtask : subtasks.keySet()) {
            list.add(subtasks.get(subtask));
        }
        return list;
    }

    public int getNextId() {
        return nextId;
    }

    public int setNextId(int id) {
        if (nextId <= 1) {
            nextId = getTaskList().size()+getEpicList().size()+getSubtaskList().size()+1;
        }
        return nextId;
    }

    @Override
    public void save() {

    }


    protected boolean isEpic(TaskTemplate task) {
        return task.getClass().equals(Epic.class);
    }

    boolean isSubtask(TaskTemplate task) {
        return task.getClass().equals(Subtask.class);
    }

    private boolean isSubtaskItself(TaskTemplate task) {
        return ((Subtask) task).getIdEpic().equals(task.getId());
    }

    private boolean isSubtaskToSubtask(TaskTemplate task) {
        Subtask subtask = (Subtask) task;
        Task checkTask = subtasks.get(subtask.getIdEpic());
        return (checkTask.getClass().equals(Subtask.class));
    }

    // Вызов просмотра истории
    public List<TaskTemplate> history() {
        setNextId(nextId);
        return historyManager.getHistory();
    }
}



