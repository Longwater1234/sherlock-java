import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
* Minified Java version of sherlock project
* https://github.com/sherlock-project/sherlock
* Search username (args) across major social networks.
*/
public class Main {
  static final int NUMTHREADS = Runtime.getRuntime().availableProcessors();
  public static final ExecutorService executor = Executors.newFixedThreadPool(NUMTHREADS);
  static Type websiteType = new TypeToken<List<Website>>() {}.getType();
  static Gson gson = new Gson();

  public static void main(String[] args) throws Exception {
    final String username = "jack";

    List<Website> websites = gson.fromJson(new BufferedReader(new FileReader("./websites.json")), websiteType);

    websites.forEach(w -> executor.execute(new SearchProcessor(w.getUrl(), username, w.getService())));

    executor.shutdown();

  }
}