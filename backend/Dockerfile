FROM openjdk:17 AS builder

WORKDIR /app
COPY . .
RUN ./gradlew build -x test

FROM openjdk:17
WORKDIR /app
COPY --from=builder /app/build/libs/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
