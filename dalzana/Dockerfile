# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml mvnw mvnw.cmd ./
RUN chmod +x mvnw
COPY .mvn .mvn
RUN ./mvnw -q -DskipTests dependency:go-offline
COPY src src
RUN ./mvnw -q -DskipTests clean package

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/dalzana-0.0.1-SNAPSHOT.jar app.jar
ENV JAVA_OPTS="-Dfile.encoding=UTF-8"
EXPOSE 8080
CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
