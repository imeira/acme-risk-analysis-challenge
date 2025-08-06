# ACME Risk Analysis Challenge

## Descrição

Este projeto implementa uma solução de análise de risco de transações financeiras baseada em microsserviços, desenvolvida em Java 17 com Spring Boot e Docker.

## Arquitetura

A solução é composta por três microsserviços principais:

1. **Risk Analysis Service** (Porta 8080): Orquestra o fluxo de análise de risco
2. **Lists Service** (Porta 8081): Gerencia listas permissivas e restritivas
3. **Decision Engine Service** (Porta 8082): Aplica regras de negócio e calcula score de risco

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3.2.0
- Maven
- Docker & Docker Compose
- H2 Database (em memória)
- Lombok

## Como Executar

### Pré-requisitos

- Docker
- Docker Compose

### Executando a aplicação

1. Clone o repositório ou extraia o arquivo ZIP
2. Navegue até o diretório raiz do projeto
3. Execute o comando:

```bash
docker-compose up --build
```

4. Aguarde todos os serviços subirem (pode levar alguns minutos na primeira execução)

### Verificando se os serviços estão funcionando

- Risk Analysis Service: http://localhost:8080/risk-analysis/health
- Lists Service: http://localhost:8081/lists/health
- Decision Engine Service: http://localhost:8082/decision-engine/health

## APIs Disponíveis

### Risk Analysis Service

#### POST /risk-analysis
Realiza análise de risco de uma transação.

**Request Body:**
```json
{
  "cpf": "12345678901",
  "ip": "192.168.1.1",
  "deviceId": "550e8400-e29b-41d4-a716-446655440000",
  "txType": "PIX",
  "txValue": 1500.00
}
```

**Response:**
```json
{
  "txDecision": "Aprovada"
}
```

### Lists Service

#### POST /lists/check
Verifica se CPF, IP ou Device ID estão em listas.

**Request Body:**
```json
{
  "cpf": "12345678901",
  "ip": "192.168.1.1",
  "deviceId": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### POST /lists/reload
Recarrega as listas a partir do arquivo de configuração.

### Decision Engine Service

#### POST /decision-engine/calculate-score
Calcula o score de risco baseado nas regras configuradas.

#### Gerenciamento de Regras (CRUD)
- GET /decision-engine/rules - Lista todas as regras
- POST /decision-engine/rules - Cria uma nova regra
- GET /decision-engine/rules/{id} - Busca regra por ID
- PUT /decision-engine/rules/{id} - Atualiza uma regra
- DELETE /decision-engine/rules/{id} - Exclui uma regra

## Configuração de Regras

O sistema vem pré-configurado com regras padrão baseadas nos requisitos do desafio:

### Regras de Valor da Transação (DEFAULT)
- R$ 0,01 - R$ 300: +200 pontos
- R$ 301 - R$ 5.000: +300 pontos
- R$ 5.001 - R$ 20.000: +400 pontos
- Acima de R$ 20.000: +500 pontos

### Regras de Listas (DEFAULT)
- CPF na lista permissiva: -200 pontos
- CPF na lista restritiva: +400 pontos
- IP na lista restritiva: +400 pontos
- Device na lista restritiva: +400 pontos

### Regras Específicas para CARTÃO
- R$ 0,01 - R$ 300: +300 pontos (ao invés de 200)
- CPF na lista permissiva: -300 pontos (ao invés de -200)

## Configuração de Score

- **Risco Baixo**: 1 - 399 pontos → Aprovada
- **Risco Médio**: 400 - 699 pontos → Aprovada
- **Risco Alto**: 700+ pontos → Negada

## Dados de Teste

### Listas Permissivas (CPF)
- 12345678901
- 98765432100
- 11111111111

### Listas Restritivas (CPF)
- 99999999999
- 88888888888
- 77777777777
- 12345678901 (também está na permissiva)

### Listas Restritivas (IP)
- 192.168.1.100
- 10.0.0.50
- 172.16.0.25

### Listas Restritivas (Device)
- 550e8400-e29b-41d4-a716-446655440000
- 6ba7b810-9dad-11d1-80b4-00c04fd430c8
- 6ba7b811-9dad-11d1-80b4-00c04fd430c8

## Exemplo de Teste Completo

```bash
curl -X POST http://localhost:8080/risk-analysis \
  -H "Content-Type: application/json" \
  -d '{
    "cpf": "12345678901",
    "ip": "192.168.1.1",
    "deviceId": "550e8400-e29b-41d4-a716-446655440001",
    "txType": "PIX",
    "txValue": 1500.00
  }'
```

## Logs

Os logs de cada serviço podem ser visualizados com:

```bash
docker-compose logs -f [nome-do-serviço]
```

Exemplo:
```bash
docker-compose logs -f risk-analysis-service
```

## Parar a Aplicação

```bash
docker-compose down
```

