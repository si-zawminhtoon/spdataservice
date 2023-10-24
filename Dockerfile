# FROM gradle:jdk21-alpine # for java 21
FROM gradle:jdk21-alpine

# Set the working directory inside the container
WORKDIR /home/spdataservices

# Copy the Gradle build files ( build.gradle and settings.gradle)
COPY build.gradle settings.gradle /app/

# Copy the entire project
COPY ./src /app/src

WORKDIR /app

# Build the application using Gradle
RUN gradle build

# Expose the port your Spring Boot application will run on ( default is 8080)
EXPOSE 8080

# Command to run the Spring Boot Application when the container starts
CMD ["java", "-jar", "/app/build/libs/spdataservice-1.0.0.jar"]
