package prozed.io.test.operations;

import com.google.gson.Gson;
import prozed.io.test.utils.TestPropertiesReader;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class HttpClientOperations {
    private final HttpClient delegate;
    private final Gson gson;
    private final String host;
    private final String schema;
    private final int port;
    private final String basePath;

    public static HttpClientOperations createDefault() {
        return new HttpClientOperations(HttpClient.newHttpClient(), new Gson(), "localhost", "http", "");
    }

    public static HttpClientOperations createDefault(String host, String schema) {
        return new HttpClientOperations(HttpClient.newHttpClient(), new Gson(), host, schema, "");
    }

    public static HttpClientOperations createDefault(String basePath) {
        return new HttpClientOperations(HttpClient.newHttpClient(), new Gson(), "localhost", "http", basePath);
    }

    public HttpClientOperations(HttpClient delegate, Gson gson, String host, String schema) {
        this(delegate, gson, host, schema, "");
    }

    public HttpClientOperations(HttpClient delegate, Gson gson, String host, String schema, String basePath) {
        this(delegate, gson, host, schema, resolvePort(), basePath);
    }

    private HttpClientOperations(HttpClient delegate, Gson gson, String host, String schema, int port, String basePath) {
        this.delegate = delegate;
        this.gson = gson;
        this.host = host;
        this.schema = schema;
        this.port = port;
        this.basePath = normalizePath(basePath);
    }

    public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler)
            throws IOException, InterruptedException {
        return delegate.send(request, responseBodyHandler);
    }

    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
        return delegate.sendAsync(request, responseBodyHandler);
    }

    public HttpRequest.Builder request(String path) {
        return HttpRequest.newBuilder().uri(URI.create(url(path)));
    }

    public <T> DeserializedResponse<T> sendAndDeserializeWithResponse(HttpRequest request, Class<T> clazz)
            throws IOException, InterruptedException {
        HttpResponse<String> response = delegate.send(request, HttpResponse.BodyHandlers.ofString());
        T body = gson.fromJson(response.body(), clazz);
        return new DeserializedResponse<>(response.statusCode(), response.headers(), body);
    }

    public HttpResponse<String> sendAndDeserializeWithRawResponse(HttpRequest request)
            throws IOException, InterruptedException {
        return delegate.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static int resolvePort() {
        return Integer.parseInt(TestPropertiesReader.getProperty("web.service.port", "8080"));
    }

    private String url(String path) {
        return "%s://%s:%d%s%s".formatted(schema, host, port, basePath, normalizePath(path));
    }

    private String normalizePath(String path) {
        if (path == null || path.isBlank() || "/".equals(path)) {
            return "";
        }
        return path.startsWith("/") ? path : "/" + path;
    }

    public record DeserializedResponse<T>(int statusCode, HttpHeaders headers, T body) {
    }
}
