#!/bin/bash

echo "=== Teste das APIs dos Microsserviços ==="
echo

# Função para testar se um serviço está rodando
test_service() {
    local service_name=$1
    local url=$2
    echo "Testando $service_name..."
    
    if curl -s -f "$url" > /dev/null; then
        echo "✓ $service_name está funcionando"
    else
        echo "✗ $service_name não está respondendo"
    fi
    echo
}

# Função para testar análise de risco
test_risk_analysis() {
    echo "=== Testando Análise de Risco ==="
    echo
    
    echo "Teste 1: Transação de baixo risco"
    curl -X POST http://localhost:8080/risk-analysis \
      -H "Content-Type: application/json" \
      -d '{
        "cpf": "12345678901",
        "ip": "192.168.1.1",
        "deviceId": "550e8400-e29b-41d4-a716-446655440001",
        "txType": "PIX",
        "txValue": 100.00
      }' 2>/dev/null | jq '.' || echo "Erro na requisição"
    echo
    
    echo "Teste 2: Transação de alto risco"
    curl -X POST http://localhost:8080/risk-analysis \
      -H "Content-Type: application/json" \
      -d '{
        "cpf": "99999999999",
        "ip": "192.168.1.100",
        "deviceId": "550e8400-e29b-41d4-a716-446655440000",
        "txType": "PIX",
        "txValue": 25000.00
      }' 2>/dev/null | jq '.' || echo "Erro na requisição"
    echo
}

# Verificar se jq está instalado
if ! command -v jq &> /dev/null; then
    echo "jq não encontrado. Executando script de instalação..."
    
    # Torna o script de instalação executável (Linux/macOS)
    if [[ "$OSTYPE" != "msys" && "$OSTYPE" != "win32" ]]; then
        chmod +x "$(dirname "$0")/install_jq.sh"
    fi
    
    # Executa o script de instalação
    if "$(dirname "$0")/install_jq.sh"; then
        # Recarrega o PATH para garantir que o jq esteja disponível
        if [ -f ~/.bashrc ]; then
            source ~/.bashrc
        elif [ -f ~/.zshrc ]; then
            source ~/.zshrc
        fi
        
        # Verifica novamente se o jq está disponível
        if ! command -v jq &> /dev/null; then
            echo "Erro: jq não pôde ser instalado automaticamente."
            echo "Por favor, instale o jq manualmente e tente novamente."
            exit 1
        fi
    else
        echo "Erro ao executar o script de instalação."
        exit 1
    fi
fi

# Aguardar um pouco para os serviços iniciarem
echo "Aguardando serviços iniciarem..."
sleep 10

# Testar health checks
test_service "Decision Engine Service" "http://localhost:8082/decision-engine/health"
test_service "Lists Service" "http://localhost:8081/lists/health"
test_service "Risk Analysis Service" "http://localhost:8080/risk-analysis/health"

# Testar análise de risco
test_risk_analysis

echo "=== Testes concluídos ==="

