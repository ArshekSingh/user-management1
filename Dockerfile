FROM openjdk:11.0
ARG JAR_FILE=account/target/kyc-1.0.0.jar
ENV env uat
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=${env}","/app.jar"]
