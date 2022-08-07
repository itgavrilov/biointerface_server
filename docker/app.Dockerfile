FROM openjdk:16
WORKDIR /app
COPY ./build/libs/biointerface_server-0.0.1-SNAPSHOT.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "./app.jar"]