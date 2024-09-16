# Use the official OpenJDK 21 image as a parent image
FROM eclipse-temurin:21-jre-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the empty log file to the container (ensures directory is created)
COPY src/main/resources/logs/cache-actions.log /app/src/main/resources/logs/cache-actions.log

# Set the correct permissions for the logs directory
RUN chmod -R 777 /app/src/main/resources/logs

# Copy the executable JAR file to the working directory
COPY target/gymhub-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the port the application runs on
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
