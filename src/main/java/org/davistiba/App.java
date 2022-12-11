package org.davistiba;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;

/**
 * Minified version of sherlock-project
 * Search given Username in 1000 Social Networks.
 * FOR JAVA 11+
 *
 * @author Davis Tibbz
 */
public class App {
    private static final ExecutorService executor = Executors.newWorkStealingPool();
    static final Gson gson = new Gson();
    static final AtomicInteger FOUND = new AtomicInteger(0);
    static final AtomicInteger NOTFOUND = new AtomicInteger(0);
    private static final String USERAGENT = "curl/7.64.1";

    public static void main(String[] args) throws Exception {
        if (args.length == 0) throw new Exception("Username is null. Bye");
        final String username = args[0];
        if (!username.matches("^[a-zA-Z0-9_-]{2,}$")) {
            throw new Exception("Username is invalid");
        }

        Website[] websites = gson.fromJson(Files.newBufferedReader(Paths.get("websites.json")), Website[].class);

        long start = System.nanoTime();
        System.out.printf("Has began at %s \n", LocalDateTime.now());
        System.out.printf("Searching for %s... \n", username);

        List<CompletableFuture<Void>> cfList = Arrays.stream(websites)
                .map(w -> doSearch(username, w.getUrl())
                        .thenApply(HttpResponse::statusCode)
                        .exceptionally(Object::hashCode)
                        .thenAcceptAsync(result -> handleResult(result, w.getUrl()), executor))
                .collect(Collectors.toList());

        CompletableFuture<?>[] mama = cfList.toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(mama).join();

        System.out.printf("Time elapsed (ms): %.3f\n", (System.nanoTime() - start) / 1e6);
        System.out.println("TOTAL FOUND: " + FOUND.get());
        System.out.println("TOTAL NOTFOUND: " + NOTFOUND.get());
        executor.shutdown();
    }

    /**
     * Our actual Search method
     *
     * @param username Username
     * @param uri      target address
     * @return response "Promise"
     */
    public static CompletableFuture<HttpResponse<String>> doSearch(String username, @NotNull String uri) {
        String finalUri = uri.replace("%", username);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(finalUri))
                .header("User-Agent", USERAGENT)
                .timeout(Duration.ofMillis(3000))
                .GET()
                .build();

        return HttpClient.newHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString());

    }

    public static void handleResult(int result, String url) {
        switch (result) {
            case 301:
            case 302:
            case 200:
                System.out.printf("\u001B[32m✓ EXISTS at %s\u001B[0m \n", url);
                FOUND.incrementAndGet();
                break;
            case 404:
                System.out.printf("\u001B[31mx NOT FOUND at %s\u001B[0m \n", url);
                NOTFOUND.incrementAndGet();
                break;
            default:
                System.out.printf("FAILED at %s \n", url);
                break;
        }

    }

}
