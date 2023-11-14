# For java 21 use amazoncorretto Docker Official Image
FROM amazoncorretto:21

# Set the working directory inside the container
WORKDIR /app

# Expose the port your Spring Boot application will run on ( default is 8080)
EXPOSE 8080

# Command to run the Spring Boot Application when the container starts
# Using gradle wrapper command for version consistency
ENTRYPOINT ["/app/gradlew","bootRun"]
