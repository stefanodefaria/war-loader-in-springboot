# War loader in Spring Boot

This project is a POC for loading .war files into a spring-boot application.
It is a good example of how .war endpoints can have their own connector (port), and even use client authentication
without affecting regular spring endpoints.

### Reference Documentation
Build:
```
mvn clean package
```

Run:
```
java -jar target/war-loader-in-springboot*.jar
```

Then, the following endpoints will be available:
- https://localhost:8080/spring
- https://localhost:8081/war (requires client auth with clientkey.p12 located in the resources directory)