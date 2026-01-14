package org.davistiba;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

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
        if (args.length == 0) throw new IllegalArgumentException("Username arg is missing. Bye!");
        final String username = args[0];

        if (!USERNAME_REGEX.matcher(username).matches()) {
            throw new IllegalArgumentException("Username pattern is invalid");
        }

        //Load json file from 'resources'
        URL websiteFile = App.class.getClassLoader().getResource("websites.json");
        assert websiteFile != null;
        Website[] websites = gson.fromJson(new BufferedReader(new InputStreamReader(websiteFile.openStream())), Website[].class);

        long start = System.nanoTime();
        System.out.printf("Loaded %d websites\n", websites.length);
        System.out.printf("Has began at %s \n", LocalDateTime.now());
        System.out.printf("Searching for %s... \n", username);

        CompletableFuture<?>[] cfList = Arrays.stream(websites)
                .map(w -> doSearch(username, w.getUrl())
                        .handle((resp, ex) -> ex == null ? resp.statusCode() : -1)
                        .thenAcceptAsync(code -> handleResult(code, w.getUrl()), executor))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(cfList).join();

        System.out.printf("Time elapsed (ms): %.3f\n", (System.nanoTime() - start) / 1.00E6);
        System.out.println("TOTAL FOUND: " + FOUND.get());
        System.out.println("TOTAL NOTFOUND: " + NOTFOUND.get());
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);
    }

    /**
     * Our actual Search method
     *
     * @param username Username
     * @param uri      target address
     * @return response "Promise"
     */
    public static CompletableFuture<HttpResponse<String>> doSearch(final String username, @NotNull String uri) {
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
     *
     * @param result Http status code
     * @param url    the target endpoint
     */
    public static void handleResult(int result, String url) {
        switch (result) {
            case 301, 302, 200 -> {
                System.out.println("\u001B[32m✓ EXISTS at " + url + "\u001B[0m");
                FOUND.incrementAndGet();
            }
            case 404 -> {
                System.out.println("\u001B[31mx NOT FOUND at " + url + "\u001B[0m");
                NOTFOUND.incrementAndGet();
            }
            default -> System.out.println("FAILED at " + url);
        }
    }
}