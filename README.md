# sherlock-java
Minified Java version of sherlock project (https://github.com/sherlock-project/sherlock). Requires Java 11 or above.
- Accepts only 1 username at a time. 
- Lookup a username from list of 1000 social networks and gives report.
- Super fast and lightweight. (REASON: uses `Async` model instead of traditional java.lang.Threads)
- **Credits**: Social website list taken from https://github.com/qeeqbox/social-analyzer)

### How to Use
- Run within your IDE, or build executable JAR with: `mvn clean package`.
- Remember to pass the username as an `arg` of Main method as shown below.
- If using an IDE, (eg. Eclipse or IntelliJ) you can pass the arg in: **Run Menu** > **Edit Configuration** > **Arguments**
- Make sure the file [websites.json](websites.json) is in the classpath, or same path as the JAR.
```bash
# For example, looking up username "davis":
java -jar sherlock-java-1.0.jar davis
```
