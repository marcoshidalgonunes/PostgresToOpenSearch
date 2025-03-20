FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /opt/app
COPY .mvn/ .mvn
COPY mvnw mvnw
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY ./src ./src
RUN ./mvnw clean install -DskipTests
 
FROM eclipse-temurin:21-jre-jammy
WORKDIR /opt/app
EXPOSE 8081
COPY --from=builder /opt/app/target/*.jar /opt/app/*.jar
ENV JAVA_TOOL_OPTIONS="\
  -Djava.util.logging.ConsoleHandler.level=FINEST \
  -Dspring.profiles.active=docker"
ENTRYPOINT ["java", "-jar", "/opt/app/*.jar" ]
