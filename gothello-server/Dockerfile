# Build
FROM maven:3.6.3-openjdk-15 as build
WORKDIR /app
COPY . ./
RUN MAVEN_CONFIG="" mvn install

# RUNTIME
FROM openjdk:15-jdk-alpine3.11
COPY --from=build /app/target/*.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar", "--server.port=${PORT}"]
