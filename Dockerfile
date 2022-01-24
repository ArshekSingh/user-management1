FROM openjdk:11.0
ARG JAR_FILE=user/target/*.jar
ENV env uat
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=${env}","/app.jar"]
