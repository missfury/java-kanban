package api.servers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class HttpTaskServer {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final int PORT = 8080;
    private final TaskManager taskManager;
    private final HttpServer server;
    private static Gson gson = new Gson();


    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHandler());
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен. Порт: " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP-сервер остановлен");
    }

    class TasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) {

            String path = httpExchange.getRequestURI().getPath();
            String method = httpExchange.getRequestMethod();
            String query = httpExchange.getRequestURI().getQuery();
            String response = "";
            int responseCode = 404;

            try {
                switch (method) {
                    case "GET":
                        if (Pattern.matches("^/tasks$", path)) {
                            response = gson.toJson(taskManager.getPrioritizedTasks());
                            responseCode = 200;
                        } else if (Pattern.matches("^/tasks/task$", path)) {
                            response = getTask(query);
                            responseCode = 200;
                        } else if (Pattern.matches("^/tasks/subtask/epic$", path) && query != null) {
                            response = gson.toJson(taskManager.getSubtasksByEpicId(getTaskIdFromQuery(query)));
                            responseCode = 200;
                        } else if (Pattern.matches("^/tasks/epic$", path)) {
                            response = getEpic(query);
                            responseCode = 200;
                        } else if (Pattern.matches("^/tasks/subtask$", path)) {
                            response = getSubtask(query);
                            responseCode = 200;
                        } else if (Pattern.matches("^/tasks/history$", path)) {
                            response = gson.toJson(taskManager.getHistory());
                            responseCode = 200;
                        }
                        break;

                    case "POST":
                        if (Pattern.matches("^/tasks/task$", path)){
                            addTask(httpExchange.getRequestBody());
                            responseCode = 201;
                        }
                        if (Pattern.matches("^/tasks/epic$", path)){
                            addEpic(httpExchange.getRequestBody());
                            responseCode = 201;
                        }
                        if (Pattern.matches("^/tasks/subtask$", path)){
                            addSubtask(httpExchange.getRequestBody());
                            responseCode = 201;
                        }
                        if (responseCode != 201) {
                            System.out.println("Ошибка при добавлении задачи. Проверьте, правильно ли указан " +
                            "путь: " + path);
                        }
                        break;

                    case "DELETE":
                        if (Pattern.matches("^/tasks/task$", path)) {
                            deleteTask(query);
                            responseCode = 200;
                        } else if (Pattern.matches("^/tasks/epic$", path)) {
                            deleteEpic(query);
                            responseCode = 200;
                        } else if (Pattern.matches("^/tasks/subtask$", path)) {
                            deleteSubtask(query);
                            responseCode = 200;
                        }
                        break;
                    default:
                        System.out.println("Запрос " + method + " не поддерживается.");
                }
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                httpExchange.sendResponseHeaders(responseCode, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes(DEFAULT_CHARSET));
                }
            } catch (IOException e) {
                System.out.println("Ошибка выполнения запроса: " + e.getMessage());
            } finally {
                httpExchange.close();
            }
        }
    }

    private static int getTaskIdFromQuery(String query) {
        String[] queryArray = query.split("=");
        return Integer.parseInt(queryArray[1]);
    }

    private String getTask(String query){
        if (query == null){
            return gson.toJson(taskManager.getTaskList());
        } else {
            return gson.toJson(taskManager.getTaskByID(getTaskIdFromQuery(query)));
        }
    }

    private String getEpic(String query){
        if (query == null){
            return gson.toJson(taskManager.getEpicList());
        } else {
            return gson.toJson(taskManager.getEpicByID(getTaskIdFromQuery(query)));
        }
    }

    private String getSubtask(String query){
        if (query == null){
            return gson.toJson(taskManager.getSubtaskList());
        } else {
            return gson.toJson(taskManager.getSubtaskByID(getTaskIdFromQuery(query)));
        }
    }

    private void addTask(InputStream inputStream) throws IOException {
        String taskBody = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        Task task = gson.fromJson(taskBody, Task.class);
        taskManager.addTask(task);
    }

    private void addEpic(InputStream inputStream) throws IOException {
        String taskBody = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        Epic epic = gson.fromJson(taskBody, Epic.class);
        taskManager.addEpic(epic);
    }

    private void addSubtask(InputStream inputStream) throws IOException {
        String taskBody = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        Subtask subtask = gson.fromJson(taskBody, Subtask.class);
        taskManager.addSubtask(subtask);
    }

    private void deleteTask(String query){
        if (query == null){
            taskManager.clearTaskList();
        } else {
            taskManager.remove(getTaskIdFromQuery(query));
        }
    }

    private void deleteEpic(String query){
        if (query == null){
            taskManager.clearEpicList();
        } else {
            taskManager.remove(getTaskIdFromQuery(query));
        }
    }

    private void deleteSubtask(String query){
        if (query == null){
            taskManager.clearEpicList();
        } else {
            taskManager.remove(getTaskIdFromQuery(query));
        }
    }
}