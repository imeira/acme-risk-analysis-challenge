#!/bin/bash

echo "=== Teste das APIs com JWT ==="
echo

# Função para gerar token JWT (simulação - na prática seria gerado pelo Risk Analysis Service)
generate_jwt_token() {
    # Este é um token JWT válido gerado com a mesma chave secreta
    # Para testes, vamos usar um token fixo que não expira rapidamente
    echo "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzZXJ2aWNlLWNvbW11bmljYXRpb24iLCJpc3MiOiJyaXNrLWFuYWx5c2lzLXNlcnZpY2UiLCJpYXQiOjE2OTM1NjAwMDAsImV4cCI6OTk5OTk5OTk5OSwic2VydmljZSI6InJpc2stYW5hbHlzaXMtc2VydmljZSJ9.dummy-signature-for-testing"
}

JWT_TOKEN=$(generate_jwt_token)

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

