# ACME Risk Analysis Challenge - Arquitetura Hexagonal com JWT

## Visão Geral

Esta solução implementa um sistema de análise de risco de transações financeiras baseado em microsserviços, utilizando **Arquitetura Hexagonal (Ports and Adapters)** e **autenticação JWT** para comunicação segura entre serviços.

## Arquitetura

### Princípios da Arquitetura Hexagonal

A solução foi refatorada para seguir os princípios da Arquitetura Hexagonal, onde:

- **Domínio Central**: Contém a lógica de negócio pura, independente de frameworks e tecnologias externas
- **Portas (Ports)**: Interfaces que definem contratos de entrada e saída
- **Adaptadores (Adapters)**: Implementações concretas que conectam o domínio ao mundo externo

### Estrutura dos Serviços

Cada microsserviço segue a estrutura hexagonal:

```
src/main/java/com/acme/[service]/
├── application/          # Camada de aplicação
│   ├── port/            # Portas de entrada e saída
│   └── service/         # Serviços de aplicação (orquestração)
├── domain/              # Camada de domínio (lógica de negócio)
│   ├── model/           # Entidades de domínio
│   └── service/         # Serviços de domínio
├── infrastructure/      # Camada de infraestrutura
│   ├── adapter/         # Adaptadores de saída
│   ├── controller/      # Adaptadores de entrada (REST)
│   ├── config/          # Configurações
│   └── security/        # Componentes de segurança JWT
└── common/              # DTOs e classes comuns
```

## Microsserviços

### 1. Risk Analysis Service (Porta 8080)
- **Responsabilidade**: Orquestrador principal do fluxo de análise de risco
- **Portas de Entrada**: `RiskAnalysisPort`
- **Portas de Saída**: `ListsServicePort`, `DecisionEngineServicePort`
- **Segurança**: Gera tokens JWT para comunicação com outros serviços

### 2. Lists Service (Porta 8081)
- **Responsabilidade**: Gerencia listas permissivas e restritivas
- **Portas de Entrada**: `ListsPort`
- **Portas de Saída**: `ListsRepositoryPort`
- **Segurança**: Valida tokens JWT recebidos

### 3. Decision Engine Service (Porta 8082)
- **Responsabilidade**: Aplica regras de negócio e calcula score de risco
- **Portas de Entrada**: `DecisionEnginePort`
- **Portas de Saída**: `RuleRepositoryPort`
- **Segurança**: Valida tokens JWT recebidos

## Segurança JWT

### Implementação
- **Chave Secreta**: Compartilhada entre todos os serviços
- **Geração**: Risk Analysis Service gera tokens para chamadas internas
- **Validação**: Lists Service e Decision Engine Service validam tokens recebidos
- **Filtros**: `JwtAuthenticationFilter` intercepta e valida requisições

### Fluxo de Autenticação
1. Risk Analysis Service recebe requisição externa (sem JWT)
2. Gera token JWT para chamadas internas
3. Inclui token no header `Authorization: Bearer <token>`
4. Serviços de destino validam o token antes de processar

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** (para JWT)
- **Spring Data JPA** (Decision Engine Service)
- **H2 Database** (em memória)
- **Maven** (gerenciamento de dependências)
- **Docker & Docker Compose**
- **JWT (JSON Web Tokens)** - biblioteca `jjwt`

## Execução

### Pré-requisitos
- Docker e Docker Compose instalados
- Java 17+ (para desenvolvimento local)
- Maven 3.6+ (para desenvolvimento local)

### Executar com Docker Compose

```bash
# Construir e executar todos os serviços
docker-compose up --build

# Executar em background
docker-compose up -d --build

# Parar os serviços
docker-compose down
```

### Executar Localmente (Desenvolvimento)

```bash
# Terminal 1 - Decision Engine Service
cd decision-engine-service
mvn spring-boot:run

# Terminal 2 - Lists Service
cd lists-service
mvn spring-boot:run

# Terminal 3 - Risk Analysis Service
cd risk-analysis-service
mvn spring-boot:run
```

## Testes

### Script de Teste Automatizado

```bash
# Executar testes das APIs com JWT
./test_api_jwt.sh
```

### Testes Manuais

#### 1. Análise de Risco (Endpoint Principal)
```bash
curl -X POST http://localhost:8080/risk-analysis \
  -H "Content-Type: application/json" \
  -d '{
    "cpf": "12345678901",
    "ip": "192.168.1.100",
    "deviceId": "device123",
    "txType": "PIX",
    "txValue": 1500.00
  }'
```

#### 2. Health Checks
```bash
curl http://localhost:8080/risk-analysis/health
curl http://localhost:8081/lists/health
curl http://localhost:8082/decision-engine/health
```

#### 3. Gerenciamento de Regras (com JWT)
```bash
# Listar regras
curl -X GET http://localhost:8082/rules \
  -H "Authorization: Bearer <JWT_TOKEN>"

# Criar nova regra
curl -X POST http://localhost:8082/rules \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "name": "Nova Regra",
    "description": "Descrição da regra",
    "txType": "PIX",
    "condition": "{\"type\":\"value_range\",\"min\":\"1000\",\"max\":\"5000\"}",
    "points": 100,
    "active": true
  }'
```

## Regras de Negócio

### Faixas de Valor
- **0-300**: Baixo risco (50 pontos)
- **301-5000**: Médio risco (100 pontos)
- **5001-20000**: Alto risco (200 pontos)
- **>20000**: Muito alto risco (500 pontos)

### Listas
- **CPF Permissivo**: -50 pontos
- **CPF Restritivo**: +200 pontos
- **IP Restritivo**: +100 pontos
- **Device Restritivo**: +100 pontos

### Decisão Final
- **Score ≤ 699**: Transação Aprovada
- **Score > 699**: Transação Negada

## Monitoramento

### Logs
- Todos os serviços geram logs detalhados
- Nível DEBUG habilitado para desenvolvimento
- Logs incluem informações de JWT e fluxo de requisições

### Health Checks
- Cada serviço expõe endpoint `/health`
- Docker Compose configurado com health checks automáticos

## Desenvolvimento

### Adicionando Novas Regras
1. Criar JSON de condição no formato apropriado
2. Usar endpoint POST `/rules` do Decision Engine Service
3. Regras são aplicadas automaticamente no próximo cálculo

### Modificando Listas
1. Editar arquivo `lists-service/src/main/resources/lists.json`
2. Usar endpoint POST `/lists/reload` para recarregar

### Testando Localmente
1. Usar Postman ou curl para testes
2. Incluir header `Authorization: Bearer <token>` para serviços protegidos
3. Verificar logs para debugging

## Arquivos Importantes

- `docker-compose.yml`: Orquestração dos serviços
- `test_api_jwt.sh`: Script de testes automatizados
- `architecture_diagram.png`: Diagrama da arquitetura
- `lists.json`: Dados das listas permissivas/restritivas
- `data.sql`: Dados iniciais das regras de negócio

## Considerações de Segurança

- JWT com chave secreta compartilhada (adequado para ambiente interno)
- Tokens com expiração configurável
- Validação rigorosa de tokens em todos os endpoints protegidos
- Health checks públicos para monitoramento
- Logs não expõem informações sensíveis

## Diferenças da Versão Anterior

### Arquitetura Hexagonal
- Separação clara entre domínio, aplicação e infraestrutura
- Lógica de negócio isolada e testável
- Facilita manutenção e evolução do código

### Segurança JWT
- Comunicação segura entre microsserviços
- Autenticação baseada em tokens
- Proteção contra acesso não autorizado

### Melhorias de Design
- Código mais modular e organizados
- Responsabilidades bem definidas
- Facilita testes unitários e de integração

