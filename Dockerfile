FROM adoptopenjdk/openjdk11:jre-11.0.19_7-ubuntu
ARG JAR_FILE=user/target/*.jar
ENV env uat
ENV TZ="Asia/Kolkata"
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=${env}","/app.jar"]