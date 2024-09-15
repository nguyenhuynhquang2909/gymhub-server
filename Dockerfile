# Use the official OpenJDK 21 image as a parent image
FROM eclipse-temurin:21-jre-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the executable JAR file to the working directory
COPY target/gymhub-0.0.1-SNAPSHOT.jar /app/app.jar

# Create the logs directory and an empty cache-actions.log file
RUN mkdir -p /app/logs && touch /app/logs/cache-actions.log

# Expose the port the application runs on
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
