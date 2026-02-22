# Build
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Gradle 캐시 최적화
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew
RUN ./gradlew dependencies

# 소스 복사 후 빌드
COPY src src
RUN ./gradlew clean build -x test

# Run
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# jar 복사
COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]