# Build stage
FROM maven:3.9.6-eclipse-temurin-17-focal AS build
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn clean package

ENV OPENAI_KEY $openai_key

# Package stage
FROM eclipse-temurin:17-jdk-focal
COPY --from=build /app/target/application.jar application.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "application.jar"]
