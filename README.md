# Gothello-Server

### About

* https://www.eclipse.org/jetty/, **Jetty** is the webserver library that we are using
  * Hello World Project
    https://www.baeldung.com/jetty-embedded
  * Simple Example
    https://examples.javacodegeeks.com/enterprise-java/jetty/embedding-jetty-with-servlet/
* https://picocli.info/, picocli is a simple way to add rich command line interfaces to a java application

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
mvn compile exec:java
```

You should now be able to connect to the server now.

http://localhost:8000/api/status

## Advanced Usage

Run on a different port

```
mvn compile exec:java -Dexec.args="--port=8080"
```