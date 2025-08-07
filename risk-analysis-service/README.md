# Risk Analysis Service

O Risk Analysis Service é um microsserviço responsável por analisar o risco de transações financeiras, tomando decisões baseadas em listas de permissão/restrição e regras de negócio.

## Visão Geral

Este serviço atua como orquestrador do fluxo de análise de risco, realizando as seguintes operações:

1. Recebe requisições de análise de risco contendo dados da transação
2. Consulta serviços de listas para verificação de CPF, IP e device ID
3. Aplica regras de negócio para cálculo de score de risco
4. Retorna uma decisão (Aprovada/Negada) baseada no risco calculado

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.x**
- **Spring Security**
- **OpenAPI/Swagger** para documentação da API
- **JWT** para autenticação entre serviços
- **Maven** para gerenciamento de dependências
- **Docker** para conteinerização

## Estrutura do Projeto

O projeto segue a arquitetura hexagonal (ports and adapters) com as seguintes camadas:

- **Domain**: Contém a lógica de negócio e modelos de domínio
- **Application**: Orquestra o fluxo de análise de risco
- **Infrastructure**: Implementações concretas para comunicação externa e APIs
- **Common**: DTOs e utilitários compartilhados

## Configuração

### Variáveis de Ambiente

As seguintes variáveis de ambiente podem ser configuradas:

- `SERVER_PORT`: Porta em que o serviço irá rodar (padrão: 8080)
- `JWT_SECRET`: Chave secreta para geração de tokens JWT
- `JWT_EXPIRATION`: Tempo de expiração do token em milissegundos (padrão: 3600000 - 1 hora)

### Endpoints da API

A documentação interativa da API está disponível em:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

#### Principais Endpoints

- `POST /risk-analysis`: Realiza a análise de risco de uma transação
- `GET /risk-analysis/health`: Verifica a saúde do serviço
- `POST /auth/token`: Gera token JWT para testes
- `GET /auth/client-ids`: Lista todos os client IDs válidos

## Executando o Projeto

### Pré-requisitos

- Java 17 ou superior
- Maven 3.6 ou superior
- Docker (opcional, para execução em container)

### Executando com Maven

```bash
mvn spring-boot:run
```

### Construindo e executando com Docker

```bash
# Construir a imagem
mvn clean package

# Construir e executar o container
docker-compose up --build
```

## Autenticação

A autenticação entre serviços é feita usando JWT. Para gerar um token de teste, utilize o endpoint `/auth/token` informando um dos seguintes client IDs:

- Lists Service: `7f073c43-d91b-4138-b7f0-85f8d73490bf`
- Decision Engine: `a1b2c3d4-e5f6-7890-abcd-ef1234567890`
- Risk Analysis: `12345678-90ab-cdef-1234-567890abcdef`

## Monitoramento

O serviço expõe endpoints do Spring Boot Actuator para monitoramento:

- `/actuator/health`: Status de saúde do serviço
- `/actuator/info`: Informações da aplicação
- `/actuator/metrics`: Métricas da aplicação


---

Desenvolvido por Igor Meira - [meira.igor@gmail.com](mailto:meira.igor@gmail.com)
