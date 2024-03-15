
FROM openjdk:19-jdk

COPY target/*.jar SMS.jar

ENTRYPOINT ["java","-jar","SMS.jar"]
EXPOSE 8081