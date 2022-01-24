FROM openjdk:11.0
ARG JAR_FILE=account/target/account-1.0.0.jar
ENV env test
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=${env}","/app.jar"]
