FROM maven:3.9.9-eclipse-temurin-17 AS build

ARG MODULE_NAME
WORKDIR /workspace

COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY api-gateway api-gateway
COPY auth-service auth-service
COPY cache-service cache-service
COPY file-service file-service
COPY notification-service notification-service
COPY user-service user-service
COPY learning-service learning-service

RUN chmod +x mvnw
RUN ./mvnw -B -ntp -pl ${MODULE_NAME} -am package -DskipTests

FROM eclipse-temurin:17-jre

ARG MODULE_NAME
WORKDIR /app

COPY --from=build /workspace/${MODULE_NAME}/target/${MODULE_NAME}-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
