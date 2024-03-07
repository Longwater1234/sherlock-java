package org.davistiba;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Minified version of sherlock-project
 * Search given Username in 1000 Social Networks.
 * FOR JAVA 11+
 *
 * @author Davis Tibbz
 */
public class App {
    static final AtomicInteger FOUND = new AtomicInteger(0);
    static final AtomicInteger NOTFOUND = new AtomicInteger(0);
    private static final Gson gson = new Gson();
    private static final ExecutorService executor = Executors.newWorkStealingPool();
    private static final String USERAGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.2 (KHTML, like Gecko) Chrome/22.0.1216.0 Safari/537.2";
    private static final Pattern USERNAME_REGEX = Pattern.compile("^[a-zA-Z0-9_-]{2,}$");

    public static void main(String[] args) throws Exception {
        if (args.length == 0) throw new Exception("Username arg is null. Bye!");
        final String username = args[0];

        if (!USERNAME_REGEX.matcher(username).matches()) {
            throw new Exception("Username is invalid");
        }

        //Load json file from 'resources' dir
        URL websiteFile = App.class.getClassLoader().getResource("websites.json");
        assert websiteFile != null;
        Website[] websites = gson.fromJson(new BufferedReader(new InputStreamReader(websiteFile.openStream())), Website[].class);

        long start = System.nanoTime();
        System.out.printf("Loaded %d websites\n", websites.length);
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
        executor.awaitTermination(120, TimeUnit.SECONDS);
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

    /**
     * Print result of given URL lookup, then update count atomically
     * @param result Http status code
     * @param url the target endpoint
     */
    public static void handleResult(int result, String url) {
        switch (result) {
            case 301, 302, 200 -> {
                System.out.printf("\u001B[32m✓ EXISTS at %s\u001B[0m \n", url);
                FOUND.incrementAndGet();
            }
            case 404 -> {
                System.out.printf("\u001B[31mx NOT FOUND at %s\u001B[0m \n", url);
                NOTFOUND.incrementAndGet();
            }
            default -> System.out.printf("FAILED at %s \n", url);
        }
    }

}
