# sherlock-java
Minified Java version of sherlock project (https://github.com/sherlock-project/sherlock). Requires Java 11 or above.
- Accepts only 1 username at a time. 
- Lookup a username from list of 1000 social networks and gives report.
- Super fast and lightweight. (REASON: uses `Async` model instead of traditional java.lang.Threads)
- **Credits**: Social website list taken from https://github.com/qeeqbox/social-analyzer)

### How to Use
- Build the jar using Maven or your IDE, and run as shown in below.
- Username (only 1 name) should be passed as an `arg` of Main method.
- Make sure the file `websites.json` is in the classpath, or same path as the JAR.
- You can also run inside your IDE, just make sure you setup **Run Configuration** for your IDE to pass arguments.
- For example, looking for username "davis":
```
java -jar sherlock.jar davis
```
