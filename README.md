# Gothello-Server

### About

We are using Spring as our webserver since its easy-ish to use
https://spring.io/guides/gs/rest-service/

## Getting Started

We use maven to manage our dependencies, tests, and builds.

https://maven.apache.org/

### Steps

Install maven.
```
sudo apt install maven
```
Compile & Run. This should install all dependencies, compile, then run.
```
cd gothello-server
mvn install
./mvnw spring-boot:run 
```

You should now be able to connect to the server now.

http://localhost:8080/greeting
