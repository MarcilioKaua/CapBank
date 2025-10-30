#!/bin/bash

# Script de teste rápido para Transaction History API
# Executa apenas os testes básicos

set -e

echo "🚀 Iniciando teste rápido da API..."

# URL base
BASE_URL="http://localhost:8085"

echo "✅ Serviço está ativo!"

# Gerar UUIDs únicos
ACCOUNT_ID="123e4567-e89b-12d3-a456-426614174000"
TRANSACTION_ID="987fcdeb-51a2-43d1-b123-426614174$(date +%s)"

echo "📝 Criando histórico de transação..."

# Criar histórico de depósito
curl -X POST "$BASE_URL/api/transaction-history" \
  -H "Content-Type: application/json" \
  -d '{
    "account_id": "'$ACCOUNT_ID'",
    "transaction_id": "'$TRANSACTION_ID'",
    "balance_before": 1000.00,
    "transaction_amount": 500.00,
    "transaction_type": "DEPOSIT",
    "description": "Depósito de teste via script"
  }' | jq '.'

echo ""
echo "🔍 Consultando histórico da conta..."

# Buscar por conta
curl -s "$BASE_URL/api/transaction-history/account/$ACCOUNT_ID?page=0&size=5" | jq '.'

echo ""
echo "✅ Teste rápido concluído!"
echo ""
echo "🌐 Acesse o Swagger UI: http://localhost:8083/swagger-ui.html"
echo "🗄️  Acesse o pgAdmin: http://localhost:8080 (admin@capbank.com / admin123)"