
FROM openjdk:18-jdk-alpine
VOLUME /main-app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-DPOSTGRES_URL=db:5432/postgres","-DPOSTGRES_USERNAME=postgres","-DPOSTGRES_PASSWORD=finances-api","-DJWT_SECRET=LAlZlh)Kc&2X6}K4lbuW#aeXFd7AqU#dt6EXe^N;)Kc&2X6}K4lbuW#aeXFd7AqU#ZX_EZNwrBYcqRAOY4YTg3ckTsB3:0$E+:T)vF5baZlHMLHIx1ztSjW9xUaf8zJUXxXKLAlZlhdt6EXe^N;)Kc&2X6}K4lbuW#aeXFB`m","-jar","/app.jar"]