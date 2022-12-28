package Manager;

import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;

import java.util.*;

import static Tasks.TaskStatus.*;

public class TaskManager {

    private final Map<Integer, Task> tasks;
    private final Map<Integer, Subtask> subtasks;
    private final Map<Integer, Epic> epics;
    private Integer nextId;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.nextId = 1;
    }

    // Добавление задачи типа "одиночная задача"
    public Integer addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return nextId - 1;
    }

    // Добавление задачи типа "масштабная задача с подзадачами"
    public Integer addEpic (Epic epic) {
        epic.setId(nextId++);
        epic.setStatus(NEW);
        List<Integer> epicList = updateSubtasksEpicInside(epic);
        epic.setSubtaskList(epicList);
        epics.put(epic.getId(), epic);
        return epic.getId();

    }

    // Добавление задачи типа "подзадача в составе масштабной задачи"
    public Integer addSubtask(Subtask subtask) {
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
    private void updateStatusOfEpic(Integer epicId) {

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
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    //Обновление масштабной задачи
    public void updateEpic(Epic epic) {
        Epic previousVersionOfEpic = epics.get(epic.getId());
        for (Integer subtaskId : previousVersionOfEpic.getSubtaskList()) {
            remove(subtaskId);
        }

        epics.put(epic.getId(), epic);
        updateStatusOfEpic(epic.getId());
    }

    //Обновление подзадачи
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getIdEpic());
        updateSubtasksEpicInside(epic);
        updateStatusOfEpic(subtask.getIdEpic());
    }

    //Удаление всех одиночных задач
    public void clearTaskList() {
        tasks.clear();
    }

    //Удаление всех масштабных задач
    public void clearEpicList() {
        epics.clear();
        subtasks.clear();
    }

    //Удаление всех подзадач
    public void clearSubtaskList() {
        subtasks.clear();
    }

    //Удаление подзадачи по ID
    public void removeSubtask(Integer id) {
        subtasks.remove(id);
    }

    //Удаление масштабной задачи по ID
    public void removeEpic(Integer id) {
        for (Integer key : subtasks.keySet()) {
            if (Objects.equals(subtasks.get(key).getIdEpic(), id)) {
                subtasks.remove(key);
                if (subtasks.size() <= 1) {
                    subtasks.clear();
                    break;
                }
            }
        }
        epics.remove(id);
    }

    //Удаление задач разного типа по идентификатору
    public void remove(Integer id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (subtasks.containsKey(id)) {
            removeSubtask(id);
        } else if (epics.containsKey(id)) {
            removeEpic(id);
        } else {
            System.out.println("ID не существует");
        }
    }

    //Получение списка подзадач, принадлежащих к масштабной задаче
    public List<Subtask> getSubtasksByEpicId(Integer id) {
        List<Subtask> list = new ArrayList<>();
        for (Integer subtask : epics.get(id).getSubtaskList()) {
            list.add(subtasks.get(subtask));
        }
        return list;
    }

    //Получение одиночной задачи по идентификатору
    public Task getTaskByID(Integer id) {
        return tasks.get(id);
    }

    //Получение масштабной задачи по идентификатору
    public Epic getEpicByID(Integer id) {
        return epics.get(id);
    }

    //Получение подзадачи по идентификатору
    public Subtask getSubtaskByID(Integer id) {
        return subtasks.get(id);
    }

    //Получение списка одиночных задач
    public List<Task> getTaskList() {
        List<Task> list = new ArrayList<>();
        for (Integer task : tasks.keySet()) {
            list.add(tasks.get(task));
        }
        return list;
    }

    //Получение списка масштабных задач
    public List<Epic>getEpicList() {
        List<Epic> list = new ArrayList<>();
        for (Integer epic : epics.keySet()) {
            list.add(epics.get(epic));
        }
        return list;
    }

    //Получение списка подзадач
    public List<Subtask>getSubtaskList() {
        List<Subtask> list = new ArrayList<>();
        for (Integer subtask : subtasks.keySet()) {
            list.add(subtasks.get(subtask));
        }
        return list;
    }
}



