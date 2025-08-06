#!/bin/bash

echo "=== Teste das APIs com JWT ==="
echo

# Função para verificar se o shell script de geração de JWT existe e é executável
check_jwt_script() {
    if [ ! -f "./generate_jwt.sh" ]; then
        echo "ERRO: Arquivo generate_jwt.sh não encontrado."
        echo "Certifique-se de que o arquivo está no mesmo diretório do script."
        exit 1
    fi
    
    # Tornar o script executável
    chmod +x ./generate_jwt.sh 2>/dev/null || true
}

# Função para verificar se as dependências necessárias estão instaladas
check_dependencies() {
    local missing_deps=0
    
    if ! command -v jq &> /dev/null; then
        echo "ERRO: 'jq' não está instalado."
        echo "Instale com: sudo apt-get install jq (Linux) ou brew install jq (macOS)"
        missing_deps=1
    fi
    
    if ! command -v openssl &> /dev/null; then
        echo "ERRO: 'openssl' não está instalado."
        echo "Instale o OpenSSL no seu sistema operacional."
        missing_deps=1
    fi
    
    if [ $missing_deps -ne 0 ]; then
        exit 1
    fi
}

# Verificar dependências e script
check_dependencies
check_jwt_script

# Gerar token JWT e remover quaisquer caracteres de nova linha
JWT_TOKEN=$(./generate_jwt.sh | tr -d '[:space:]')

if [ -z "$JWT_TOKEN" ]; then
    echo "ERRO: Falha ao gerar o token JWT." >&2
    exit 1
fi

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

echo "5. Testando acesso sem JWT (deve falhar)..."
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

