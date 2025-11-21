#FROM eclipse-temurin:21-jre-jammy
#VOLUME /tmp
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} app.jar
#EXPOSE 9090
#ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre-jammy
VOLUME /tmp
COPY target/server-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/app.jar"]