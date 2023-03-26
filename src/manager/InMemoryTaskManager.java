package manager;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static tasks.TaskStatus.*;

public class InMemoryTaskManager implements TaskManager {

    protected int id;
    protected static Map<Integer, Task> tasks;
    protected static Map<Integer, Subtask> subtasks;
    protected static Map<Integer, Epic> epics;
    protected Integer nextId;
    protected static HistoryManager historyManager;
    protected Map<Integer, TaskTemplate> taskMap;
    protected Set<TaskTemplate> sortedTasks;

    public InMemoryTaskManager() {
        id = 0;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        this.nextId = 1;
        historyManager = Managers.getDefaultHistory();
        taskMap = new HashMap<>();
        sortedTasks = new TreeSet<>(Comparator.comparing(TaskTemplate::getId));
    }

    // Добавление задачи типа "одиночная задача"
    @Override
    public Integer addTask(Task task) {
        nextId = getNextId();
        task.setId(nextId);
        if (task.getStatus() == null) {
            task.setStatus(NEW);
        }
        tasks.put(task.getId(), task);
        return nextId++;
    }


    // Добавление задачи типа "масштабная задача с подзадачами"
    @Override
    public Integer addEpic (Epic epic) {
        nextId = getNextId();
        epic.setId(nextId);
        epic.setStatus(NEW);
        List<Integer> epicList = updateSubtasksEpicInside(epic);
        epic.setSubtaskList(epicList);
        epics.put(epic.getId(), epic);
        return nextId++;

    }

    // Добавление задачи типа "подзадача в составе масштабной задачи"
    @Override
    public Integer addSubtask(Subtask subtask) {
        nextId = getNextId();
        subtask.setId(nextId);
        if (subtask.getStatus() == null) {
            subtask.setStatus(NEW);
        }
        subtasks.put(subtask.getId(), subtask);
        List<Integer> subtaskList = updateSubtasksEpicInside(epics.get(subtask.getIdEpic()));
        Epic epic = epics.get(subtask.getIdEpic());
        updateEpicStartTimeAndDuration(subtask.getIdEpic());
        subtaskList.add(subtask.getId());
        epic.setSubtaskList(subtaskList);
        epics.put(subtask.getIdEpic(), epic);
        return nextId++;
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
        addSortedTask(task);
        boolean isTimeCrossed = checkTaskTimeCrossing(task);
        if (isTimeCrossed) {
            System.out.println("Задача " + task.getName() + " пересекается с другой задачей");
            task.setStartTime("11:11 11.11.1111");
            task.setDuration(0);
        }
    }

    //Обновление масштабной задачи
    @Override
    public void updateEpic(Epic epic) {
        int id = epic.getId();
        epic.setSubtaskList(epics.get(id).getSubtaskList());
        epics.put(id, epic);
        historyManager.updateId(epics.get(id));
        updateStatusOfEpic(id);
        updateEpicStartTimeAndDuration(id);
    }

    //Обновление подзадачи
    @Override
    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        subtasks.put(id, subtask);
        historyManager.updateId(subtasks.get(id));
        Epic epic = epics.get(subtask.getIdEpic());
        updateSubtasksEpicInside(epic);
        updateStatusOfEpic(subtask.getIdEpic());
        updateEpicStartTimeAndDuration(subtask.getIdEpic());
        addSortedTask(subtask);
        boolean isTimeCrossed = checkTaskTimeCrossing(subtask);
        if (isTimeCrossed) {
           System.out.println("Задача " + subtask.getName() + " пересекается с другой задачей");
            subtask.setStartTime("11:11 11.11.1111");
            subtask.setDuration(0);
        }
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
        historyManager.remove(id);
        int epicID = getSubtaskByID(id).getIdEpic();
        epics.get(epicID).deleteSubtask(id);
        updateEpicStartTimeAndDuration(getSubtaskByID(id).getIdEpic());
        subtasks.remove(id);
    }

    //Удаление масштабной задачи по ID
    @Override
    public void removeEpic(Integer id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (Integer subtaskId : epic.getSubtaskList()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            historyManager.remove(id);
            epics.remove(id);
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
        historyManager.add(task);

        return task;

    }

    //Получение масштабной задачи по идентификатору
    @Override
    public Epic getEpicByID(Integer id) {
        setNextId(nextId);
        Epic epic = epics.getOrDefault(id, null);
        historyManager.add(epic);
        return epic;
    }

    //Получение подзадачи по идентификатору
    @Override
    public Subtask getSubtaskByID(Integer id) {
        setNextId(nextId);
        historyManager.add(subtasks.get(id));
        updateEpicStartTimeAndDuration(subtasks.get(id).getIdEpic());
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

    @Override
    public Collection<TaskTemplate> getAllTasks() {
        taskMap.putAll(tasks);
        taskMap.putAll(epics);
        taskMap.putAll(subtasks);
        return taskMap.values();
    }

    @Override
    public List<TaskTemplate> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Collection<TaskTemplate> getPrioritizedTasks() {

        Collection<TaskTemplate> allTasks = getAllTasks();
        Collection<TaskTemplate> result = new ArrayList<>();
        Collection<TaskTemplate> nullStartTime = new ArrayList<>();
        Collection<TaskTemplate> haveStartTime = new ArrayList<>();

        if (allTasks != null) {
            if (sortedTasks != null && sortedTasks.size() == allTasks.size()) {
                result = new ArrayList<>(sortedTasks);
            } else {
                for (TaskTemplate task : allTasks) {
                    if (task.getStartTime() == null) {
                        nullStartTime.add(task);
                    } else {
                        haveStartTime.add(task);
                    }
                }
                result = haveStartTime
                        .stream()
                        .sorted(Comparator.comparing(TaskTemplate::getStartTime))
                        .collect(Collectors.toList());
                sortedTasks = new TreeSet<>(Comparator.comparing(TaskTemplate::getId));
                sortedTasks.addAll(result);
            }
            result.addAll(nullStartTime);
        }

        return result;
    }

    private void addSortedTask(TaskTemplate task) {
        sortedTasks.add(task);
    }

    // Вызов просмотра истории
    public List<TaskTemplate> history() {
        return historyManager.getHistory();
    }

   public void updateEpicStartTimeAndDuration(Integer id) {
        Epic epic = epics.get(id);
        List<TaskTemplate> subtasks = List.copyOf(getSubtasksByEpicId(id));
        if (subtasks != null && !subtasks.isEmpty()) {
            LocalDateTime earlyStartTime = subtasks.get(0).getStartTime();
            LocalDateTime latestEndTime = subtasks.get(0).getStartTime();
            if (earlyStartTime == null) {
                earlyStartTime = LocalDateTime.MAX;
                latestEndTime = LocalDateTime.MIN;
            }
            Duration durationSum = Duration.ZERO;
            for (TaskTemplate sub : subtasks) {

                if (sub.getDuration() != null) {
                    durationSum = durationSum.plus(sub.getDuration());
                }

                LocalDateTime subStartTime = sub.getStartTime();
                if (subStartTime != null && subStartTime.isBefore(earlyStartTime)) {
                    earlyStartTime = subStartTime;
                }

                LocalDateTime subEndTime = sub.getEndTime();
                if (subStartTime != null && subEndTime.isAfter(latestEndTime)) {
                    latestEndTime = subEndTime;
                }
            }
            if (earlyStartTime.equals(LocalDateTime.MAX)) {
                epic.resetStartTime();
            } else {
                epic.setStartTime(earlyStartTime);
            }
            if (durationSum.equals(Duration.ZERO)) {
                epic.resetDuration();
            } else {
                epic.setDuration(durationSum);
            }
            if (!latestEndTime.equals(LocalDateTime.MIN)) {
                epic.setEndTime(latestEndTime);
            }
        }
    }

    private boolean checkTaskTimeCrossing(TaskTemplate task) {
        LocalDateTime taskStartTime = task.getStartTime();
        LocalDateTime taskEndTime = task.getEndTime();
        for (TaskTemplate eachTask : sortedTasks) {
            LocalDateTime anotherTaskStartTime = eachTask.getStartTime();
            LocalDateTime anotherTaskEndTime = eachTask.getEndTime();
            if (task.getType().equals(TypeOfTask.SUBTASK)) {
                Integer epicId = ((Subtask) task).getIdEpic();
                if (eachTask.getId().equals(epicId)) {
                    return false;
                }
            } else if (task.getType().equals(TypeOfTask.EPIC)) {
                Collection<Integer> subtasksList = ((Epic) task).getSubtaskList();
                if (subtasksList.contains(eachTask.getId())) {
                    return false;
                }
            } else if (!task.equals(eachTask)
                    && taskStartTime.isAfter(anotherTaskStartTime)
                    && taskEndTime.isBefore(anotherTaskEndTime)) {
                return true;
            }
        }
        return false;
    }

}



