FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiar o arquivo pom.xml e baixar dependências
COPY pom.xml .
COPY src ./src

# Instalar Maven
RUN apt-get update && apt-get install -y maven

# Compilar a aplicação
RUN mvn clean package -DskipTests

# Expor a porta
EXPOSE 8082

# Executar a aplicação
CMD ["java", "-jar", "target/decision-engine-service-1.0.0.jar"]

