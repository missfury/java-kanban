package manager;

import tasks.TaskTemplate;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    public Node<TaskTemplate> head;
    public Node<TaskTemplate> tail;
    private Map<Integer, Node<TaskTemplate>> historyMap = new HashMap<>();


    // Отметить задачу в листе просмотренных
    @Override
    public void historyAdd(TaskTemplate task) {
        if (task != null) {
            int id = task.getId();
            remove(id);
            linkLast(task);
            historyMap.put(task.getId(), tail);
            System.out.println("\r\nЗадача просмотрена");
        } else {
            System.out.println("\r\nЗадачи с таким ID не существует");
        }
    }

    // Отображение последних просмотренных пользователем задач
    @Override
    public List<TaskTemplate> getHistory() {
        return getTasks();
    }


    // Удаление задачи из просмотренных
    @Override
    public void remove(int id) {
        removeNode(historyMap.get(id));
        historyMap.remove(id);
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


