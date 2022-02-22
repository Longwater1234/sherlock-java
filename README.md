# sherlock-java
Minified Java version of sherlock project (https://github.com/sherlock-project/sherlock). Requires Java 11 or above.
- Accepts only 1 username at a time. 
- Uses Gson library for JSON decoding.
- Uses built-in HTTP Client (Java 11 and above) to make http requests.
- Username should be passed as an `arg` of Main method, without any prefixes.
- Build the jar using your IDE, and run as shown in below. (e.g. looking up username "davis")
```
java -jar sherlock.jar davis
```
