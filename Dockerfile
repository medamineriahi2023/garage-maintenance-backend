FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/stock-management-0.0.1-SNAPSHOT.jar app.jar
RUN ls
ENTRYPOINT ["java", "-jar", "/app/app.jar"]