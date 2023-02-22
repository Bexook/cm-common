FROM maven:3.8.6-openjdk-11
FROM openjdk:11
WORKDIR /app
COPY src /app
COPY pom.xml /app
RUN mvn clean deploy

