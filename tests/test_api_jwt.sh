#!/bin/bash

echo "=== Teste das APIs com JWT ==="
echo

# Função para verificar se as dependências necessárias estão instaladas
check_dependencies() {
    local missing_deps=0
    
    if ! command -v jq &> /dev/null; then
        echo "ERRO: 'jq' não está instalado."
        echo "Instale com: sudo apt-get install jq (Linux) ou brew install jq (macOS)"
        missing_deps=1
    fi
    
    if ! command -v curl &> /dev/null; then
        echo "ERRO: 'curl' não está instalado."
        echo "Instale o cURL no seu sistema operacional."
        missing_deps=1
    fi
    
    if [ $missing_deps -ne 0 ]; then
        exit 1
    fi
}

# Verificar dependências
check_dependencies

# Obter token JWT do serviço de autenticação
echo "Obtendo token JWT do serviço de autenticação..."
RESPONSE=$(curl -s "http://localhost:8080/auth/token?clientId=7f073c43-d91b-4138-b7f0-85f8d73490bf")
echo "RESPONSE= $RESPONSE"

if [ $? -ne 0 ]; then
    echo "ERRO: Falha ao obter o token JWT do serviço de autenticação." >&2
    echo "Certifique-se de que o serviço Risk Analysis está em execução." >&2
    exit 1
fi

# Extrair o token da resposta
JWT_TOKEN=$(echo "$RESPONSE" | jq -r '.token' 2>/dev/null)

if [ -z "$JWT_TOKEN" ] || [ "$JWT_TOKEN" = "null" ]; then
    echo "ERRO: Resposta inesperada do serviço de autenticação:" >&2
    echo "$RESPONSE" >&2
    exit 1
fi

echo "Token JWT obtido com sucesso!"
echo "Authorization: Bearer $JWT_TOKEN"

echo "1. Testando Health Checks (sem JWT)..."
echo "Risk Analysis Service:"
curl -s http://localhost:8080/risk-analysis/health
echo
echo "Lists Service:"
curl -s http://localhost:8081/lists/health
echo
echo "Decision Engine Service:"
curl -s http://localhost:8082/decision-engine/health
echo
echo

echo "2. Testando Lists Service com JWT..."
curl -X POST http://localhost:8081/lists/check \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "cpf": "12345678901",
    "ip": "192.168.1.100",
    "deviceId": "device123"
  }'
echo
echo

echo "3. Testando Decision Engine Service com JWT..."
curl -X POST http://localhost:8082/decision-engine/calculate-score \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "cpf": "12345678901",
    "ip": "192.168.1.100",
    "deviceId": "device123",
    "txType": "PIX",
    "txValue": 1500.00,
    "cpfInPermissiveList": false,
    "cpfInRestrictiveList": false,
    "ipInRestrictiveList": false,
    "deviceIdInRestrictiveList": false
  }'
echo
echo

echo "4. Testando Risk Analysis Service (fluxo completo)..."
curl -X POST http://localhost:8080/risk-analysis \
  -H "Content-Type: application/json" \
  -d '{
    "cpf": "12345678901",
    "ip": "192.168.1.100",
    "deviceId": "device123",
    "txType": "PIX",
    "txValue": 1500.00
  }'
echo
echo

echo "5. Testando acesso sem JWT (deve falhar!!!)..."
echo "Lists Service sem JWT:"
curl -X POST http://localhost:8081/lists/check \
  -H "Content-Type: application/json" \
  -d '{
    "cpf": "12345678901",
    "ip": "192.168.1.100",
    "deviceId": "device123"
  }'
echo
echo

echo "6. Listando regras do Decision Engine..."
curl -X GET http://localhost:8082/rules \
  -H "Authorization: Bearer $JWT_TOKEN"
echo
echo

echo "=== Testes concluídos ==="

