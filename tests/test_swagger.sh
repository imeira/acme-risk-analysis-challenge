#!/bin/bash

echo "=== Teste de Validação do Swagger UI ==="
echo

# Função para testar se o Swagger UI está acessível
test_swagger_ui() {
    local service_name=$1
    local port=$2
    local url="http://localhost:$port/swagger-ui.html"
    
    echo "Testando Swagger UI do $service_name..."
    
    # Testa se a página Swagger UI está acessível
    response=$(curl -s -L -o /dev/null -w "%{http_code}" "$url")
    
    if [ "$response" = "200" ]; then
        echo "✅ $service_name: Swagger UI acessível em $url"
    else
        echo "❌ $service_name: Swagger UI não acessível (HTTP $response)"
    fi
    
    # Testa se a documentação OpenAPI JSON está disponível
    api_docs_url="http://localhost:$port/v3/api-docs"
    api_response=$(curl -s -o /dev/null -w "%{http_code}" "$api_docs_url")
    
    if [ "$api_response" = "200" ]; then
        echo "✅ $service_name: OpenAPI docs disponíveis em $api_docs_url"
    else
        echo "❌ $service_name: OpenAPI docs não disponíveis (HTTP $api_response)"
    fi
    
    echo
}

# Função para verificar se um serviço está rodando
check_service() {
    local service_name=$1
    local port=$2
    local health_endpoint=$3
    
    echo "Verificando se $service_name está rodando..."
    
    response=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:$port$health_endpoint")
    
    if [ "$response" = "200" ]; then
        echo "✅ $service_name está rodando na porta $port"
        return 0
    else
        echo "❌ $service_name não está rodando na porta $port (HTTP $response)"
        return 1
    fi
}

echo "1. Verificando se os serviços estão rodando..."
echo

# Verifica se os serviços estão rodando
risk_running=false
lists_running=false
decision_running=false

if check_service "Risk Analysis Service" 8080 "/risk-analysis/health"; then
    risk_running=true
fi

if check_service "Lists Service" 8081 "/lists/health"; then
    lists_running=true
fi

if check_service "Decision Engine Service" 8082 "/decision-engine/health"; then
    decision_running=true
fi

echo
echo "2. Testando interfaces Swagger UI..."
echo

# Testa Swagger UI apenas para serviços que estão rodando
if [ "$risk_running" = true ]; then
    test_swagger_ui "Risk Analysis Service" 8080
fi

if [ "$lists_running" = true ]; then
    test_swagger_ui "Lists Service" 8081
fi

if [ "$decision_running" = true ]; then
    test_swagger_ui "Decision Engine Service" 8082
fi

echo "3. Instruções de Acesso:"
echo
echo "Se os serviços estiverem rodando, acesse as interfaces Swagger UI em:"
echo "• Risk Analysis Service: http://localhost:8080/swagger-ui.html"
echo "• Lists Service: http://localhost:8081/swagger-ui.html"
echo "• Decision Engine Service: http://localhost:8082/swagger-ui.html"
echo
echo "Para executar os serviços, use:"
echo "docker-compose up --build"
echo
echo "=== Teste concluído ==="

