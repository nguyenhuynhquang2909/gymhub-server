# Use the official OpenJDK 17 image as a parent image
FROM eclipse-temurin:21-jre-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the executable JAR file to the working directory
COPY target/gymhub-0.0.1-SNAPSHOT.jar /app/app.jar

# Copy the logs directory to the working directory
COPY src/main/resources/logs /app/logs

# Expose the port the application runs on
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
