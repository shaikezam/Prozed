package prozed.io.test.operations;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class HttpClientOperations {
    private final HttpClient delegate;
    private final Gson gson;

    public static HttpClientOperations createDefault() {
        return new HttpClientOperations(HttpClient.newHttpClient(), new Gson());
    }

    public HttpClientOperations(HttpClient delegate, Gson gson) {
        this.delegate = delegate;
        this.gson = gson;
    }

    public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler)
            throws IOException, InterruptedException {
        return delegate.send(request, responseBodyHandler);
    }

    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
        return delegate.sendAsync(request, responseBodyHandler);
    }

    public <T> T sendAndDeserialize(HttpRequest request, Class<T> clazz) throws IOException, InterruptedException {
        return sendAndDeserialize(request, TypeToken.get(clazz).getType());
    }

    // 2. For complex/generic types (e.g., new TypeToken<List<User>>(){}.getType())
    public <T> T sendAndDeserialize(HttpRequest request, Type typeOfT) throws IOException, InterruptedException {
        HttpResponse<String> response = delegate.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 300) {
            throw new IOException("HTTP Request failed: " + response.statusCode());
        }

        return gson.fromJson(response.body(), typeOfT);
    }

    public HttpClient getDelegate() {
        return delegate;
    }
}