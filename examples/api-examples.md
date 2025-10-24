# 📚 Exemplos de API - Transaction History Service

Este documento contém exemplos práticos de uso da API do Transaction History Service.

## 🔗 URL Base

```
http://localhost:8083/transaction-service/api/v1
```

## 📝 Exemplos de Requisições

### 1. Health Check

**Verificar se o serviço está funcionando:**



### 2. Criar Histórico de Depósito

**Requisição:**
```bash
curl -X POST http://localhost:8083/transaction-service/api/v1/transaction-history \
  -H "Content-Type: application/json" \
  -d '{
    "account_id": "123e4567-e89b-12d3-a456-426614174000",
    "transaction_id": "987fcdeb-51a2-43d1-b123-426614174001",
    "balance_before": 1000.00,
    "transaction_amount": 500.00,
    "transaction_type": "DEPOSIT",
    "description": "Depósito via ATM"
  }'
```

**Resposta esperada (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "account_id": "123e4567-e89b-12d3-a456-426614174000",
  "transaction_id": "987fcdeb-51a2-43d1-b123-426614174001",
  "balance_before": 1000.00,
  "balance_after": 1500.00,
  "transaction_amount": 500.00,
  "transaction_type": "DEPOSIT",
  "status": "SUCCESS",
  "description": "Depósito via ATM",
  "record_date": "2024-10-22T14:30:00"
}
```

---

### 3. Criar Histórico de Saque

**Requisição:**
```bash
curl -X POST http://localhost:8083/transaction-service/api/v1/transaction-history \
  -H "Content-Type: application/json" \
  -d '{
    "account_id": "123e4567-e89b-12d3-a456-426614174000",
    "transaction_id": "987fcdeb-51a2-43d1-b123-426614174002",
    "balance_before": 1500.00,
    "transaction_amount": 200.00,
    "transaction_type": "WITHDRAWAL",
    "description": "Saque no caixa eletrônico"
  }'
```

**Resposta esperada (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "account_id": "123e4567-e89b-12d3-a456-426614174000",
  "transaction_id": "987fcdeb-51a2-43d1-b123-426614174002",
  "balance_before": 1500.00,
  "balance_after": 1300.00,
  "transaction_amount": 200.00,
  "transaction_type": "WITHDRAWAL",
  "status": "SUCCESS",
  "description": "Saque no caixa eletrônico",
  "record_date": "2024-10-22T15:45:00"
}
```

---

### 4. Criar Histórico de Transferência

**Requisição:**
```bash
curl -X POST http://localhost:8083/transaction-service/api/v1/transaction-history \
  -H "Content-Type: application/json" \
  -d '{
    "account_id": "123e4567-e89b-12d3-a456-426614174000",
    "transaction_id": "987fcdeb-51a2-43d1-b123-426614174003",
    "balance_before": 1300.00,
    "transaction_amount": 300.00,
    "transaction_type": "TRANSFER",
    "description": "Transferência para conta poupança"
  }'
```

**Resposta esperada (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440002",
  "account_id": "123e4567-e89b-12d3-a456-426614174000",
  "transaction_id": "987fcdeb-51a2-43d1-b123-426614174003",
  "balance_before": 1300.00,
  "balance_after": 1000.00,
  "transaction_amount": 300.00,
  "transaction_type": "TRANSFER",
  "status": "SUCCESS",
  "description": "Transferência para conta poupança",
  "record_date": "2024-10-22T16:20:00"
}
```

---

### 5. Buscar Histórico por ID

**Requisição:**
```bash
curl -X GET http://localhost:8083/transaction-service/api/v1/transaction-history/550e8400-e29b-41d4-a716-446655440000
```

**Resposta esperada (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "account_id": "123e4567-e89b-12d3-a456-426614174000",
  "transaction_id": "987fcdeb-51a2-43d1-b123-426614174001",
  "balance_before": 1000.00,
  "balance_after": 1500.00,
  "transaction_amount": 500.00,
  "transaction_type": "DEPOSIT",
  "status": "SUCCESS",
  "description": "Depósito via ATM",
  "record_date": "2024-10-22T14:30:00"
}
```

---

### 6. Buscar Histórico por Conta (Básico)

**Requisição:**
```bash
curl -X GET "http://localhost:8083/transaction-service/api/v1/transaction-history/account/123e4567-e89b-12d3-a456-426614174000?page=0&size=10"
```

**Resposta esperada (200 OK):**
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440002",
      "account_id": "123e4567-e89b-12d3-a456-426614174000",
      "transaction_id": "987fcdeb-51a2-43d1-b123-426614174003",
      "balance_before": 1300.00,
      "balance_after": 1000.00,
      "transaction_amount": 300.00,
      "transaction_type": "TRANSFER",
      "status": "SUCCESS",
      "description": "Transferência para conta poupança",
      "record_date": "2024-10-22T16:20:00"
    },
    {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "account_id": "123e4567-e89b-12d3-a456-426614174000",
      "transaction_id": "987fcdeb-51a2-43d1-b123-426614174002",
      "balance_before": 1500.00,
      "balance_after": 1300.00,
      "transaction_amount": 200.00,
      "transaction_type": "WITHDRAWAL",
      "status": "SUCCESS",
      "description": "Saque no caixa eletrônico",
      "record_date": "2024-10-22T15:45:00"
    },
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "account_id": "123e4567-e89b-12d3-a456-426614174000",
      "transaction_id": "987fcdeb-51a2-43d1-b123-426614174001",
      "balance_before": 1000.00,
      "balance_after": 1500.00,
      "transaction_amount": 500.00,
      "transaction_type": "DEPOSIT",
      "status": "SUCCESS",
      "description": "Depósito via ATM",
      "record_date": "2024-10-22T14:30:00"
    }
  ],
  "page_number": 0,
  "page_size": 10,
  "total_elements": 3,
  "total_pages": 1,
  "first": true,
  "last": true
}
```

---

### 7. Buscar Histórico com Filtros

**Filtrar apenas depósitos:**
```bash
curl -X GET "http://localhost:8083/transaction-service/api/v1/transaction-history/account/123e4567-e89b-12d3-a456-426614174000?transactionType=DEPOSIT&page=0&size=10"
```

**Filtrar por período:**
```bash
curl -X GET "http://localhost:8083/transaction-service/api/v1/transaction-history/account/123e4567-e89b-12d3-a456-426614174000?startDate=2024-10-22T00:00:00&endDate=2024-10-22T23:59:59&page=0&size=10"
```

**Filtros combinados:**
```bash
curl -X GET "http://localhost:8083/transaction-service/api/v1/transaction-history/account/123e4567-e89b-12d3-a456-426614174000?transactionType=WITHDRAWAL&startDate=2024-10-22T15:00:00&endDate=2024-10-22T18:00:00&page=0&size=5&sortBy=recordDate&sortDirection=ASC"
```

---

## ❌ Exemplos de Erros

### 1. Dados Inválidos (400 Bad Request)

**Requisição com dados inválidos:**
```bash
curl -X POST http://localhost:8083/transaction-service/api/v1/transaction-history \
  -H "Content-Type: application/json" \
  -d '{
    "account_id": "",
    "transaction_id": "",
    "balance_before": -100,
    "transaction_amount": -50,
    "transaction_type": null,
    "description": "Dados inválidos"
  }'
```

**Resposta de erro (400 Bad Request):**
```json
{
  "timestamp": "2024-10-22T16:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input parameters",
  "path": "/api/v1/transaction-history",
  "validation_errors": [
    {
      "field": "accountId",
      "message": "Account ID cannot be blank"
    },
    {
      "field": "transactionId",
      "message": "Transaction ID cannot be blank"
    },
    {
      "field": "balanceBefore",
      "message": "Balance before must be positive"
    },
    {
      "field": "transactionAmount",
      "message": "Transaction amount must be positive"
    },
    {
      "field": "transactionType",
      "message": "Transaction type cannot be null"
    }
  ]
}
```

### 2. Histórico Duplicado (400 Bad Request)

**Tentar criar histórico com transaction_id já existente:**
```bash
# Mesmo transaction_id usado anteriormente
curl -X POST http://localhost:8083/transaction-service/api/v1/transaction-history \
  -H "Content-Type: application/json" \
  -d '{
    "account_id": "123e4567-e89b-12d3-a456-426614174000",
    "transaction_id": "987fcdeb-51a2-43d1-b123-426614174001",
    "balance_before": 2000.00,
    "transaction_amount": 100.00,
    "transaction_type": "DEPOSIT",
    "description": "Tentativa de duplicação"
  }'
```

**Resposta de erro (400 Bad Request):**
```json
{
  "timestamp": "2024-10-22T16:35:00",
  "status": 400,
  "error": "Business Rule Violation",
  "message": "Transaction history already exists for transaction: 987fcdeb-51a2-43d1-b123-426614174001",
  "path": "/api/v1/transaction-history"
}
```

### 3. Histórico Não Encontrado (404 Not Found)

**Buscar por ID inexistente:**
```bash
curl -X GET http://localhost:8083/transaction-service/api/v1/transaction-history/00000000-0000-0000-0000-000000000000
```

**Resposta de erro (404 Not Found):**
```http
HTTP/1.1 404 Not Found
Content-Length: 0
```

---

## 🧪 Testes com jq (formatação JSON)

Para respostas mais legíveis, use `jq`:

```bash
# Buscar histórico formatado
curl -s "http://localhost:8083/transaction-service/api/v1/transaction-history/account/123e4567-e89b-12d3-a456-426614174000?page=0&size=5" | jq '.'

# Extrair apenas os IDs dos históricos
curl -s "http://localhost:8083/transaction-service/api/v1/transaction-history/account/123e4567-e89b-12d3-a456-426614174000?page=0&size=10" | jq '.content[].id'

# Contar total de transações
curl -s "http://localhost:8083/transaction-service/api/v1/transaction-history/account/123e4567-e89b-12d3-a456-426614174000?page=0&size=1" | jq '.total_elements'

# Ver apenas depósitos
curl -s "http://localhost:8083/transaction-service/api/v1/transaction-history/account/123e4567-e89b-12d3-a456-426614174000?transactionType=DEPOSIT" | jq '.content[] | {id, amount: .transaction_amount, date: .record_date}'
```

---

## 📊 Testando Performance

**Criar múltiplas transações em sequência:**
```bash
#!/bin/bash
ACCOUNT_ID="123e4567-e89b-12d3-a456-426614174000"
BASE_URL="http://localhost:8083/transaction-service/api/v1/transaction-history"

for i in {1..10}; do
    TRANSACTION_ID=$(uuidgen)
    curl -X POST "$BASE_URL" \
      -H "Content-Type: application/json" \
      -d '{
        "account_id": "'$ACCOUNT_ID'",
        "transaction_id": "'$TRANSACTION_ID'",
        "balance_before": 1000.00,
        "transaction_amount": 50.00,
        "transaction_type": "DEPOSIT",
        "description": "Teste de performance #'$i'"
      }' &
done
wait
echo "Todas as requisições enviadas!"
```

---

## 💡 Dicas

- **Use UUIDs únicos** para `transaction_id` para evitar conflitos
- **Valores monetários** devem ser positivos
- **Paginação** máxima é de 100 registros por página
- **Filtros de data** usam formato ISO 8601: `yyyy-MM-dd'T'HH:mm:ss`
- **Ordenação** pode ser `ASC` ou `DESC`
- **Use jq** para formatar respostas JSON de forma legível

---

**Para mais exemplos, consulte o Swagger UI: http://localhost:8083/transaction-service/swagger-ui.html** 🚀