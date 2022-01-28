package org.davistiba;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Minified version of sherlock-project
 * Search given Username on major Social Networks.
 * FOR JAVA 11+
 */
public class App {
    private static final int NUMTHREADS = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService executor = Executors.newFixedThreadPool(NUMTHREADS);
    static Type websiteType = new TypeToken<List<Website>>() {}.getType();
    static Gson gson = new Gson();

    public static void main(String[] args) throws Exception {
        if (args.length == 0) throw new Exception("Username is null. Bye");
        final String username = args[0];
        if (!username.matches("^[a-zA-Z0-9_-]{2,}$")) throw new Exception("Username is invalid");

        List<Website> websites = gson.fromJson(new BufferedReader(new FileReader("websites.json")), websiteType);

        websites.forEach(w -> search(username, w.getUrl())
                .thenApplyAsync(HttpResponse::statusCode, executor)
                .exceptionally(Object::hashCode)
                .thenAccept(result -> handleResult(result, w.getUrl())));

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
               executor.shutdown();
            }
        }, 70000L);
        
    }

	/**
	 * Our actual Search method
	 *
	 * @param username Username
	 * @param uri      target address
	 * @return response "Promise"
	 */
	public static CompletableFuture<HttpResponse<String>> search(String username, @NotNull String uri) {
		String finalUri = uri.replace("%", username);
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(finalUri))
				.timeout(Duration.ofSeconds(20))
				.GET()
				.build();

		return HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString());

	}


	public static void handleResult(int result, String url) {
		switch (result) {
		case 200:
			System.out.printf("\u001B[32m✓ EXISTS at %s\u001B[0m \n", url);
			break;
		case 404:
			System.out.printf("\u001B[31mx NOT FOUND at %s\u001B[0m \n", url);
			break;
		default:
			System.out.printf("FAILED at %s \n", url);
			break;
		}

	}

}
