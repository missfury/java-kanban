package manager;

import tasks.TaskTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    public Node<TaskTemplate> head;
    public Node<TaskTemplate> tail;
    private  int size = 0;
    private final Map<Integer, Node<TaskTemplate>> historyMap = new HashMap<>();


    // Отметить задачу в листе просмотренных
    @Override
    public void add(TaskTemplate task) {
        if (task != null) {
            int idTask = task.getId();
            remove(idTask);
            linkLast(task);
            historyMap.put(idTask, tail);

        } else {
            removeNode(tail);
        }
    }


    // Отображение последних просмотренных пользователем задач
    @Override
    public List<TaskTemplate> getHistory() {
        return getTasks();
    }

    public int getSize() {
        return size;
    }


    // Удаление задачи из просмотренных
    @Override
    public void remove(int id) {
        removeNode(historyMap.get(id));
        historyMap.remove(id);
    }

    //
    @Override
    public void updateId(TaskTemplate task) {
        int idTask = task.getId();
        historyMap.put(idTask, tail);
    }

    // Добавление задачи в конец списка
    public Node<TaskTemplate> linkLast(TaskTemplate task) {
        final Node<TaskTemplate> oldTail = tail;
        final Node<TaskTemplate> newNode = new Node<>(tail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        return newNode;

    }


    // Представление списка просмотренных задач в виде ArrayList
    public List<TaskTemplate> getTasks() {
        List<TaskTemplate> tasks = new ArrayList<>();
        if (head != null) {
            tasks.add(head.getItem());
            Node<TaskTemplate> next = head.getNext();
            while (next != null) {
                tasks.add(next.getItem());
                next = next.getNext();
            }
        }
        return tasks;
    }
    
    // Удаление узла связного списка
    private void removeNode(Node<TaskTemplate> node) {
        if (!(node == null)) {
            final Node<TaskTemplate> next = node.next;
            final Node<TaskTemplate> prev = node.prev;
            node.item = null;

            if (head == node && tail == node) {
                head = null;
                tail = null;
            } else if (head == node && !(tail == node)) {
                head = next;
                head.prev = null;
            } else if (!(head == node) && tail == node) {
                tail = prev;
                tail.next = null;
            } else {
                prev.next = next;
                next.prev = prev;
            }

        }
    }

}

class Node<T> {
    public T item;
    public Node<T> next;
    public Node<T> prev;

    Node(Node<T> prev, T item, Node<T> next) {

        this.item = item;
        this.next = next;
        this.prev = prev;
    }


    public T getItem() {
        return this.item;
    }

    public Node<T> getNext() {
        return this.next;
    }
}


