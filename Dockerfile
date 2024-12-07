FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built Spring Boot application JAR file into the container
# Replace 'your-app.jar' with the name of your actual JAR file
COPY ./target/stock-management-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your Spring Boot app listens on
EXPOSE 8080

# Set the entrypoint to run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]