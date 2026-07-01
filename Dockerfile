#
# EyaLove - Build stage
#
FROM maven:3.9-eclipse-temurin-17-alpine AS build
ENV HOME=/home/app
WORKDIR $HOME
# Cache dependencies first for faster rebuilds
COPY pom.xml $HOME
RUN mvn -B dependency:go-offline --fail-never
COPY . $HOME
RUN mvn -B clean package -Dmaven.test.skip=true

#
# EyaLove - Runtime stage
#
FROM eclipse-temurin:17-jre
ENV HOME=/home/app
WORKDIR $HOME
# JVM options can be overridden at runtime (e.g. -e JAVA_OPTS="-Xmx512m")
ENV JAVA_OPTS="-Xmx512m -XX:+HeapDumpOnOutOfMemoryError"
ENV SPRING_PROFILES_ACTIVE=prod
# Version-agnostic: copy whatever jar Maven produced
COPY --from=build $HOME/target/*.jar $HOME/eyalove.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dfile.encoding=UTF-8 -jar eyalove.jar"]
