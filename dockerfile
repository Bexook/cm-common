FROM maven:3.8.6-openjdk-11 AS BUILDER
WORKDIR /app
COPY settings.xml /app
COPY src /app
COPY pom.xml /app
RUN mvn clean deploy -s settings.xml

