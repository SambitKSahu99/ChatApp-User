FROM openjdk:17-jdk-slim
ADD build/libs/ChatApp-UserManagement-0.0.1-SNAPSHOT.jar ChatApp-UserManagement-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/ChatApp-UserManagement-0.0.1-SNAPSHOT.jar"]
EXPOSE 8081