FROM adoptopenjdk/openjdk11
ARG JAR_FILE=user/target/*.jar
ENV env uat
ENV TZ="Asia/Kolkata"
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=${env}","/app.jar"]