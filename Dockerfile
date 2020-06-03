# Build
FROM maven:3.6.3-openjdk-15 as build
WORKDIR /app
COPY . ./
RUN MAVEN_CONFIG="" ./mvnw clean install

# RUNTIME
FROM openjdk:15-jdk-alpine3.11
COPY --from=build /app/target/*.jar /app.jar
EXPOSE 80
CMD ["java", "-jar", "/app.jar", "--server.port=80"]
