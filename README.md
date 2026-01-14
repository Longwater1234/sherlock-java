# sherlock-java

Minified Java version of sherlock project (https://github.com/sherlock-project/sherlock). Requires Java 11 or above.

- Accepts only 1 username at a time.
- Lookup a username from list of 1000 social networks and gives summary.
- Super fast and lightweight. (REASON: uses `Async` model instead of traditional java.lang.Threads)
- Social website list taken from https://github.com/qeeqbox/social-analyzer)
- PLEASE NOTE: it simply relies on the HTTP status of the requests. (200/301/302 = Found, 404 = Not found, all others =
  Failed)
- Therefore, not 100% accurate. There maybe some false-positives with some sites.

## Requirements

- Java JDK 21 or later

### How to Use

- Using your Terminal (or PowerShell), build executable JAR with: `./mvnw clean package`.
- Remember to pass the username as an `arg` of Main method as shown below.
- If running inside an IDE, (eg. Eclipse or IntelliJ) you can set the username arg in: **Run Menu** > **Edit
  Configuration** >
  **Arguments**
- Make sure the file [websites.json](src/main/resources/websites.json) is in the classpath.

```bash
# For example, looking up username "davis":
java -jar target/sherlock-java.jar davis
```
