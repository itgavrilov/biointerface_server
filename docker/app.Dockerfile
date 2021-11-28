FROM openjdk:16
WORKDIR /app
COPY ./build/libs .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "./biointerface_server-0.0.1-SNAPSHOT.jar"]