# Use the official OpenJDK image with Java 17
FROM openjdk:17-oracle

# Set the working directory inside the container
WORKDIR /app

# Copy the fat jar file from your host to your current location (inside the image)
# Replace 'app.jar' with the actual name of your fat jar file
COPY ./app/target/app-0.0.1-SNAPSHOT.jar /app/app.jar

# Run the jar file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Optional: If your application uses a particular port, expose it.
EXPOSE 8080
