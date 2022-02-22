package org.davistiba;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;


public class SearchProcessor implements Runnable {
    // final String finalUri =
    private final String finalUri;
    private final String username;
    private static final AtomicInteger FOUND = new AtomicInteger(0);
    private static final AtomicInteger NOTFOUND = new AtomicInteger(0);

    public static AtomicInteger getFOUND() {
        return FOUND;
    }

    public static AtomicInteger getNOTFOUND() {
        return NOTFOUND;
    }

    // colors
    public final String ANSI_RED = "\u001B[31m";
    public final String ANSI_RESET = "\u001B[0m";
    public final String ANSI_GREEN = "\u001B[32m";

    public SearchProcessor(String username, String uri) {
        this.finalUri = uri.replaceAll("%", username);
        this.username = username;

    }

    @Override
    public void run() {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(finalUri))
                .timeout(Duration.ofSeconds(3)).GET().build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            final int statusCode = response.statusCode();
            String exists = (statusCode == 200) ? (ANSI_GREEN + "\u2713") : (ANSI_RED + "x");
            if (statusCode == 200) FOUND.incrementAndGet();
            else if(statusCode == 404) NOTFOUND.incrementAndGet();
            System.out.printf("%s \t %s on %s? %n%s", exists, username, finalUri, ANSI_RESET);

        } catch (IOException | InterruptedException e) {
            System.out.printf("[!] failed at %s %n%s", finalUri, ANSI_RESET);
        }
    }


}