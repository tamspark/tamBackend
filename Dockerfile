
FROM openjdk:19-jdk

COPY target/*.jar SMS.jar

ENTRYPOINT ["java","-jar","TAM-tam.jar"]
EXPOSE 8081