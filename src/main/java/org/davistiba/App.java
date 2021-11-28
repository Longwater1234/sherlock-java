package org.davistiba;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Minified version of sherlock-project
 * Search given Username on major Social Networks.
 * FOR JAVA 11+
 */
public class App {
    public static final ExecutorService executor = Executors.newFixedThreadPool(4);
    static Type websiteType = new TypeToken<List<Website>>() {}.getType();
    static Gson gson = new Gson();

    public static void main(String[] args) throws Exception {
        if (args.length == 0) throw new Exception("Username is null. Bye");
        final String username = args[0];
        if(!username.matches("[a-zA-Z_0-9\\S\\-]+")) throw new Exception("Username is invalid");


        List<Website> websites = gson.fromJson(new BufferedReader(new FileReader("websites.json")), websiteType);

        websites.forEach(w -> executor.execute(new SearchProcessor(w.getUrl(), username, w.getService())));


        executor.shutdown();
    }


}
