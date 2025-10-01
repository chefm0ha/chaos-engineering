FROM openjdk:21-jdk-slim

WORKDIR /app

# Copie simple du JAR existant
COPY build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]

