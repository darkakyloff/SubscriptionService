# Используем минимальный образ с JDK 17
FROM eclipse-temurin:17-jre-alpine

# Указываем рабочую директорию
WORKDIR /app

# Копируем заранее собранный JAR-файл
COPY subscription-service.jar app.jar

# Открываем порт
EXPOSE 8080

# Команда запуска
ENTRYPOINT ["java", "-jar", "app.jar"]
