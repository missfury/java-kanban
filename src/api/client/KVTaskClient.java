package api.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class KVTaskClient {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final int PORT = 8078;
    private final String url;
    protected String apiToken;

    public KVTaskClient() {
        url = String.format("http://localhost:%d", PORT);
        apiToken = register(url);
    }

    public String register(String url) {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(String.format("%s/register", url));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                apiToken = response.body();
                return apiToken;
            } else System.out.println("Не удалось получить API_TOKEN");
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте адрес!");
        }
        return "wrong API_TOKEN";
    }

    public void put(String key, String json)  {
        HttpClient client = HttpClient.newHttpClient();
        if (apiToken == null) {
            System.out.println("API_TOKEN отсутствует");
            return;
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/save/%s?API_TOKEN=%s", url, key, apiToken)))
                .POST(HttpRequest.BodyPublishers.ofString(json, DEFAULT_CHARSET))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте адрес");
        }
    }

    public String load(String key) {
        HttpClient client = HttpClient.newHttpClient();
        if (apiToken == null) {
            return "API_TOKEN не присвоен";
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/load/%s?API_TOKEN=%s", url, key, apiToken)))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте адрес");
        }
        return "Ошибка получения запроса";
    }


}
