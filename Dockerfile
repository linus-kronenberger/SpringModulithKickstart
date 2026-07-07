FROM eclipse-temurin:25
COPY target/SpringModulithKickstart-0.0.1-SNAPSHOT.jar SpringModulithKickstart-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/SpringModulithKickstart-0.0.1-SNAPSHOT.jar"]