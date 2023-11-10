# for java 21
# FROM gradle:jdk21-alpine
# FROM bellsoft/liberica-openjdk-alpine-musl:21
FROM amazoncorretto:21

# ENV JAVA_HOME /usr/lib/jvm/jdk-21.0.1-bellsoft-x86_64
# ENV JAVA_HOME /usr/lib/jvm/java-21-amazon-corretto

# Set the working directory inside the container
WORKDIR /app

COPY . /app/

RUN ./gradlew build

# Copy the Gradle build files ( build.gradle and settings.gradle)
# COPY build.gradle settings.gradle /app/

# Copy the entire project
# COPY ./ /app/

# RUN chmod 777 /app/gradlew
# Build the application using Gradle
#RUN gradle build
# CMD ["./gradlew","clean"]
# CMD ["./gradlew","build"]
# CMD ["./gradlew","bootRun"]


# Expose the port your Spring Boot application will run on ( default is 8080)
EXPOSE 8080

# Command to run the Spring Boot Application when the container starts
CMD ["java", "-Xdebug", "-jar", "/app/build/libs/spdataservice-1.0.0.jar"]
#CMD ["./gradlew", "bootRun"]

# ENTRYPOINT ["/app/gradlew","bootRun"]
