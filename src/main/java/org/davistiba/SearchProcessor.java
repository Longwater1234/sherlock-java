package org.davistiba;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * ONLY FOR JDK 11 or above
 */
public class SearchProcessor implements Runnable {
    // final  String finalUri =
    private final String finalUri;
    private final String username;
    private final String service;

    // colors
    public final String ANSI_RED = "\u001B[31m";
    public final String ANSI_RESET = "\u001B[0m";
    public final String ANSI_GREEN = "\u001B[32m";


    public SearchProcessor(String uri, String username, String service) {
        this.finalUri = uri.replaceAll("%", username);
        this.username = username;
        this.service = service;
    }

    @Override
    public void run() {
        // ONLY FOR JAVA 11+
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(finalUri)).header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(3))
                .GET()
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            String exists = (response.statusCode() == 200) ? (ANSI_GREEN + "\u2713") : (ANSI_RED + "x");
            System.out.printf("%s \t %s on %s? %s%n", exists, username, service, ANSI_RESET);

        } catch (IOException | InterruptedException e) {
            System.out.printf("[!]\t failed at %s %n", service);
        }
    }
}
