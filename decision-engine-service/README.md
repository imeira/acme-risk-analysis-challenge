# Decision Engine Service

O Decision Engine Service é um serviço responsável por realizar análises de risco e tomar decisões baseadas em regras de negócio definidas.

## 🚀 Tecnologias e Dependências

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** - Para autenticação e autorização
- **Spring Data JPA** - Para persistência de dados
- **H2 Database** - Banco de dados em memória
- **Lombok** - Para reduzir código boilerplate
- **JJWT** - Para autenticação via JWT
- **SpringDoc OpenAPI** - Documentação da API (Swagger UI)

## 🔧 Configuração do Ambiente

### Pré-requisitos

- Java 17 ou superior
- Maven 3.6.3 ou superior
- Docker (opcional, para execução via container)

### Instalação

1. Clone o repositório:
   ```bash
   git clone [URL_DO_REPOSITÓRIO]
   cd decision-engine-service
   ```

2. Compile o projeto:
   ```bash
   mvn clean install
   ```

3. Execute a aplicação:
   ```bash
   mvn spring-boot:run
   ```
   
   Ou execute o arquivo JAR gerado:
   ```bash
   java -jar target/decision-engine-service-1.0.0.jar
   ```

### Docker

Para executar a aplicação em um container Docker:

```bash
docker build -t decision-engine-service .
docker run -p 8080:8080 decision-engine-service
```

## 📚 Documentação da API

A documentação da API está disponível através do Swagger UI:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 🔒 Autenticação

O serviço utiliza autenticação JWT (JSON Web Token). Para autenticar, faça uma requisição para o endpoint de login com as credenciais válidas.

## 🛠 Estrutura do Projeto

```
decision-engine-service/
├── src/
│   ├── main/
│   │   ├── java/com/acme/decision/
│   │   │   ├── config/         # Configurações do Spring
│   │   │   ├── controller/     # Controladores da API
│   │   │   ├── dto/            # Objetos de Transferência de Dados
│   │   │   ├── model/          # Entidades do domínio
│   │   │   ├── repository/     # Repositórios JPA
│   │   │   ├── security/       # Configurações de segurança
│   │   │   ├── service/        # Lógica de negócio
│   │   │   └── DecisionEngineServiceApplication.java
│   │   └── resources/
│   │       ├── application.yml # Configurações da aplicação
│   │       └── db/             # Scripts SQL
│   └── test/                   # Testes automatizados
├── Dockerfile
└── pom.xml
```



---

Desenvolvido por Igor Meira - [meira.igor@gmail.com](mailto:meira.igor@gmail.com)

