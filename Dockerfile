
FROM openjdk:19-jdk

COPY target/*.jar TAM-tam.jar

ENTRYPOINT ["java","-jar","TAM-tam.jar"]
EXPOSE 8081