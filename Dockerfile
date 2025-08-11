# Usamos la imagen oficial de OpenJDK 21 para compilar y correr
FROM eclipse-temurin:21-jdk-jammy AS build

# Directorio de trabajo
WORKDIR /app

# Copia archivos de proyecto y construye
COPY . .

# Compilar la app (ajusta comando si usas Gradle o Maven)
RUN ./mvnw clean package -DskipTests

# Imagen final para correr el JAR
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Expone el puerto donde corre tu app (ajusta si es otro)
EXPOSE 8080

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
