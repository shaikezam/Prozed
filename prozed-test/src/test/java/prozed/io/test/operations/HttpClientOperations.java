package prozed.io.test.operations;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class HttpClientOperations {
    private final HttpClient delegate;
    private final Gson gson;
    private final String host;
    private final String schema;

    public static HttpClientOperations createDefault() {
        return new HttpClientOperations(HttpClient.newHttpClient(), new Gson(), "localhost", "http");
    }

    public static HttpClientOperations createDefault(String host, String schema) {
        return new HttpClientOperations(HttpClient.newHttpClient(), new Gson(), host, schema);
    }

    public HttpClientOperations(HttpClient delegate, Gson gson, String host, String schema) {
        this.delegate = delegate;
        this.gson = gson;
        this.host = host;
        this.schema = schema;
    }

    public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler)
            throws IOException, InterruptedException {
        return delegate.send(request, responseBodyHandler);
    }

    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
        return delegate.sendAsync(request, responseBodyHandler);
    }

    public <T> DeserializedResponse<T> sendAndDeserializeWithResponse(HttpRequest request, Class<T> clazz)
            throws IOException, InterruptedException {
        return sendAndDeserializeWithResponse(request, TypeToken.get(clazz).getType());
    }

    public <T> DeserializedResponse<T> sendAndDeserializeWithResponse(HttpRequest request, Type typeOfT)
            throws IOException, InterruptedException {
        HttpResponse<String> response = delegate.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 300) {
            throw new IOException("HTTP Request failed: " + response.statusCode());
        }

        T body = gson.fromJson(response.body(), typeOfT);
        return new DeserializedResponse<>(response.statusCode(), response.headers(), body);
    }

    public String baseUrl(int port, String path) {
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        return "%s://%s:%d%s".formatted(schema, host, port, normalizedPath);
    }

    public record DeserializedResponse<T>(int statusCode, HttpHeaders headers, T body) {
    }
}
