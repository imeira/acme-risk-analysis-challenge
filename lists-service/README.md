# Lists Service

O Lists Service é um microsserviço Spring Boot responsável pelo gerenciamento de listas permissivas e restritivas utilizadas para análise de risco. Este serviço mantém listas de CPFs, IPs e dispositivos que podem ser consultadas por outros serviços para validação de transações e análises de segurança.

## 🚀 Tecnologias

- Java 17
- Spring Boot 3.2.0
- Spring Web
- Spring Security
- JWT (JSON Web Tokens)
- Maven
- Docker

## 🔧 Configuração

### Pré-requisitos

- Java 17 ou superior
- Maven 3.6 ou superior
- Docker (opcional, para execução via container)

### Instalação

1. Clone o repositório:
   ```bash
   git clone [URL_DO_REPOSITÓRIO]
   cd lists-service
   ```

2. Construa o projeto:
   ```bash
   mvn clean install
   ```

3. Execute o serviço:
   ```bash
   mvn spring-boot:run
   ```

### Docker

Para executar o serviço em um container Docker:

```bash
docker build -t lists-service .
docker run -p 8080:8080 lists-service
```

## 📋 Estrutura de Dados

O serviço utiliza um arquivo `lists.json` que contém as seguintes listas:

1. **CPFs Permissivos**: Lista de CPFs considerados de baixo risco
2. **CPFs Restritivos**: Lista de CPFs com restrições ou considerados de alto risco
3. **IPs Restritivos**: Lista de endereços IP bloqueados ou suspeitos
4. **Dispositivos Restritivos**: Lista de identificadores de dispositivos com restrições

## 🔒 Segurança

O serviço utiliza autenticação baseada em JWT (JSON Web Tokens). Para acessar os endpoints, é necessário incluir um token JWT válido no cabeçalho `Authorization` das requisições.

## 📊 Endpoints

A documentação completa da API está disponível em `/swagger-ui.html` quando o serviço estiver em execução no ambiente de desenvolvimento.

### Principais endpoints:

- `GET /api/lists/cpf/permissive` - Retorna a lista de CPFs permissivos
- `GET /api/lists/cpf/restrictive` - Retorna a lista de CPFs restritivos
- `GET /api/lists/ip/restrictive` - Retorna a lista de IPs restritivos
- `GET /api/lists/device/restrictive` - Retorna a lista de dispositivos restritivos
- `POST /api/lists/validate/cpf` - Valida se um CPF está em alguma lista
- `POST /api/lists/validate/ip` - Valida se um IP está na lista restritiva
- `POST /api/lists/validate/device` - Valida se um dispositivo está na lista restritiva

## 🛠️ Desenvolvimento

### Estrutura do Projeto

```
src/
  main/
    java/com/acme/lists/
      config/         # Configurações do Spring
      controller/     # Controladores REST
      model/          # Modelos de dados
      repository/     # Camada de acesso a dados
      security/       # Configurações de segurança
      service/        # Lógica de negócios
      ListsApplication.java  # Classe principal
    resources/
      application.yml # Configurações do aplicativo
      lists.json      # Dados das listas
```

### Testes

Para executar os testes:

```bash
mvn test
```

---

Desenvolvido por Igor Meira - [meira.igor@gmail.com](mailto:meira.igor@gmail.com)
