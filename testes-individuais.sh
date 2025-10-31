#!/bin/bash

# CapBank - Testes Individuais por Serviço
# Este script permite testar cada serviço separadamente

GATEWAY_URL="http://localhost:8081"
COLOR_GREEN="\033[0;32m"
COLOR_RED="\033[0;31m"
COLOR_BLUE="\033[0;34m"
COLOR_YELLOW="\033[1;33m"
COLOR_RESET="\033[0m"

# Variáveis globais
ACCESS_TOKEN=""
USER_CPF=""
USER_ID=""
ACCOUNT_ID=""
ACCOUNT_NUMBER=""

print_header() {
    echo ""
    echo -e "${COLOR_BLUE}========================================${COLOR_RESET}"
    echo -e "${COLOR_BLUE}$1${COLOR_RESET}"
    echo -e "${COLOR_BLUE}========================================${COLOR_RESET}"
}

print_success() {
    echo -e "${COLOR_GREEN}✓ $1${COLOR_RESET}"
}

print_error() {
    echo -e "${COLOR_RED}✗ $1${COLOR_RESET}"
}

print_info() {
    echo -e "${COLOR_YELLOW}ℹ $1${COLOR_RESET}"
}

extract_json_value() {
    echo "$1" | grep -o "\"$2\"[[:space:]]*:[[:space:]]*\"[^\"]*\"" | sed "s/\"$2\"[[:space:]]*:[[:space:]]*\"\([^\"]*\)\"/\1/"
}

# ============================================
# NÍVEL 1: TESTES BÁSICOS (SEM AUTENTICAÇÃO)
# ============================================

test_1_gateway_health() {
    print_header "TESTE 1: Gateway - Health Check"

    print_info "Testando: GET http://localhost:8081/actuator/health"
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/actuator/health 2>/dev/null || echo "000")

    if [ "$HTTP_CODE" = "200" ] || [ "$HTTP_CODE" = "404" ]; then
        print_success "Gateway está respondendo (HTTP $HTTP_CODE)"
        return 0
    else
        print_error "Gateway não está respondendo (HTTP $HTTP_CODE)"
        print_info "Verifique se o Gateway está rodando: docker-compose ps gateway-service"
        return 1
    fi
}

test_2_user_registration() {
    print_header "TESTE 2: User Service - Registro de Usuário"

    # Gera CPF único
    TIMESTAMP=$(date +%s)
    USER_CPF="111${TIMESTAMP: -8}"

    print_info "Testando: POST $GATEWAY_URL/api/user/register"
    print_info "CPF gerado: $USER_CPF"

    RESPONSE=$(curl -s -X POST "$GATEWAY_URL/api/user/register" \
        -H "Content-Type: application/json" \
        -d "{
            \"fullName\": \"Teste Usuario\",
            \"cpf\": \"$USER_CPF\",
            \"email\": \"teste$TIMESTAMP@example.com\",
            \"phone\": \"11999999999\",
            \"birthDate\": \"1990-01-15\",
            \"accountType\": \"CURRENT\",
            \"password\": \"Senha123\",
            \"confirmPassword\": \"Senha123\"
        }")

    USER_ID=$(extract_json_value "$RESPONSE" "id")

    if [ -n "$USER_ID" ]; then
        print_success "Usuário registrado com sucesso!"
        print_info "User ID: $USER_ID"
        print_info "CPF: $USER_CPF"

        # Salva em arquivo para testes posteriores
        echo "USER_CPF=$USER_CPF" > .test_data
        echo "USER_ID=$USER_ID" >> .test_data

        return 0
    else
        print_error "Falha no registro do usuário"
        echo "$RESPONSE"

        # Verifica se é erro de timeout/indisponibilidade
        if echo "$RESPONSE" | grep -q "503\|Unavailable\|Timeout"; then
            print_info "Erro 503: Serviço indisponível"
            print_info "Possíveis causas:"
            print_info "  1. User Service não está pronto"
            print_info "  2. Problema de comunicação com Gateway"
            print_info "  3. Dependência circular (User->Gateway->BankAccount)"
            print_info ""
            print_info "Solução temporária: Testar criação manual (sem conta automática)"
        fi

        return 1
    fi
}

test_3_user_login() {
    print_header "TESTE 3: Auth Service - Login"

    # Carrega dados do teste anterior
    if [ -f .test_data ]; then
        source .test_data
    else
        print_error "Dados de teste não encontrados. Execute o teste 2 primeiro."
        return 1
    fi

    print_info "Testando: POST $GATEWAY_URL/api/user/login"
    print_info "CPF: $USER_CPF"

    RESPONSE=$(curl -s -X POST "$GATEWAY_URL/api/user/login" \
        -H "Content-Type: application/json" \
        -d "{
            \"cpf\": \"$USER_CPF\",
            \"password\": \"Senha123\"
        }")

    ACCESS_TOKEN=$(extract_json_value "$RESPONSE" "accessToken")

    if [ -n "$ACCESS_TOKEN" ]; then
        print_success "Login realizado com sucesso!"
        print_info "Token: ${ACCESS_TOKEN:0:50}..."

        # Salva token para próximos testes
        echo "ACCESS_TOKEN=$ACCESS_TOKEN" >> .test_data

        return 0
    else
        print_error "Falha no login"
        echo "$RESPONSE"
        return 1
    fi
}

# ============================================
# NÍVEL 2: TESTES COM AUTENTICAÇÃO
# ============================================

test_4_get_user() {
    print_header "TESTE 4: User Service - Consultar Usuário"

    # Carrega dados
    if [ -f .test_data ]; then
        source .test_data
    else
        print_error "Execute os testes anteriores primeiro"
        return 1
    fi

    print_info "Testando: GET $GATEWAY_URL/api/user/$USER_CPF"

    RESPONSE=$(curl -s -X GET "$GATEWAY_URL/api/user/$USER_CPF" \
        -H "Authorization: Bearer $ACCESS_TOKEN")

    FULL_NAME=$(extract_json_value "$RESPONSE" "fullName")

    if [ -n "$FULL_NAME" ]; then
        print_success "Usuário encontrado: $FULL_NAME"
        return 0
    else
        print_error "Falha ao consultar usuário"
        echo "$RESPONSE"
        return 1
    fi
}

test_5_list_accounts() {
    print_header "TESTE 5: BankAccount Service - Listar Contas"

    # Carrega dados
    if [ -f .test_data ]; then
        source .test_data
    else
        print_error "Execute os testes anteriores primeiro"
        return 1
    fi

    print_info "Testando: GET $GATEWAY_URL/api/bankaccount"

    RESPONSE=$(curl -s -X GET "$GATEWAY_URL/api/bankaccount" \
        -H "Authorization: Bearer $ACCESS_TOKEN")

    ACCOUNT_ID=$(echo "$RESPONSE" | grep -o '"id":"[^"]*"' | head -1 | sed 's/"id":"\([^"]*\)"/\1/')
    ACCOUNT_NUMBER=$(echo "$RESPONSE" | grep -o '"accountNumber":"[^"]*"' | head -1 | sed 's/"accountNumber":"\([^"]*\)"/\1/')

    if [ -n "$ACCOUNT_ID" ]; then
        print_success "Conta encontrada!"
        print_info "Account ID: $ACCOUNT_ID"
        print_info "Número: $ACCOUNT_NUMBER"

        # Salva para próximos testes
        echo "ACCOUNT_ID=$ACCOUNT_ID" >> .test_data
        echo "ACCOUNT_NUMBER=$ACCOUNT_NUMBER" >> .test_data

        return 0
    else
        print_error "Nenhuma conta encontrada"
        print_info "Resposta:"
        echo "$RESPONSE"
        print_info ""
        print_info "Isso pode acontecer se a criação automática da conta falhou."
        print_info "Vamos tentar criar uma conta manualmente no próximo teste."
        return 1
    fi
}

test_6_create_account_manual() {
    print_header "TESTE 6: BankAccount Service - Criar Conta Manual"

    # Carrega dados
    if [ -f .test_data ]; then
        source .test_data
    else
        print_error "Execute os testes anteriores primeiro"
        return 1
    fi

    # Gera número de conta único
    ACCOUNT_NUM=$(date +%s | tail -c 10)

    print_info "Testando: POST $GATEWAY_URL/api/bankaccount"
    print_info "Número da conta: $ACCOUNT_NUM"

    RESPONSE=$(curl -s -X POST "$GATEWAY_URL/api/bankaccount" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $ACCESS_TOKEN" \
        -d "{
            \"accountNumber\": \"$ACCOUNT_NUM\",
            \"agency\": \"0001\",
            \"balance\": 0.00,
            \"accountType\": \"CURRENT\",
            \"userId\": \"$USER_ID\"
        }")

    ACCOUNT_ID=$(extract_json_value "$RESPONSE" "id")

    if [ -n "$ACCOUNT_ID" ]; then
        print_success "Conta criada com sucesso!"
        print_info "Account ID: $ACCOUNT_ID"

        # Atualiza arquivo de dados
        sed -i '/ACCOUNT_ID=/d' .test_data
        sed -i '/ACCOUNT_NUMBER=/d' .test_data
        echo "ACCOUNT_ID=$ACCOUNT_ID" >> .test_data
        echo "ACCOUNT_NUMBER=$ACCOUNT_NUM" >> .test_data

        return 0
    else
        print_error "Falha ao criar conta"
        echo "$RESPONSE"
        return 1
    fi
}

test_7_get_balance() {
    print_header "TESTE 7: BankAccount Service - Consultar Saldo"

    # Carrega dados
    if [ -f .test_data ]; then
        source .test_data
    else
        print_error "Execute os testes anteriores primeiro"
        return 1
    fi

    if [ -z "$ACCOUNT_NUMBER" ]; then
        print_error "Número da conta não encontrado. Execute o teste 5 ou 6 primeiro."
        return 1
    fi

    print_info "Testando: GET $GATEWAY_URL/api/bankaccount/$ACCOUNT_NUMBER/balance"

    RESPONSE=$(curl -s -X GET "$GATEWAY_URL/api/bankaccount/$ACCOUNT_NUMBER/balance" \
        -H "Authorization: Bearer $ACCESS_TOKEN")

    if [ -n "$RESPONSE" ]; then
        print_success "Saldo atual: R$ $RESPONSE"
        return 0
    else
        print_error "Falha ao consultar saldo"
        return 1
    fi
}

# ============================================
# NÍVEL 3: TESTES DE TRANSAÇÕES
# ============================================

test_8_deposit() {
    print_header "TESTE 8: Transaction Service - Depósito"

    # Carrega dados
    if [ -f .test_data ]; then
        source .test_data
    else
        print_error "Execute os testes anteriores primeiro"
        return 1
    fi

    if [ -z "$ACCOUNT_ID" ]; then
        print_error "Account ID não encontrado. Execute o teste 5 ou 6 primeiro."
        return 1
    fi

    print_info "Testando: POST $GATEWAY_URL/api/transaction/deposit"
    print_info "Depositando R$ 1000.00 na conta $ACCOUNT_ID"

    RESPONSE=$(curl -s -X POST "$GATEWAY_URL/api/transaction/deposit" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $ACCESS_TOKEN" \
        -d "{
            \"target_account_id\": \"$ACCOUNT_ID\",
            \"amount\": 1000.00,
            \"description\": \"Depósito de teste\"
        }")

    STATUS=$(extract_json_value "$RESPONSE" "status")

    if [ "$STATUS" = "SUCCESS" ]; then
        print_success "Depósito realizado com sucesso!"
        sleep 2

        # Verifica saldo
        print_info "Verificando novo saldo..."
        BALANCE=$(curl -s -X GET "$GATEWAY_URL/api/bankaccount/$ACCOUNT_NUMBER/balance" \
            -H "Authorization: Bearer $ACCESS_TOKEN")
        print_info "Saldo atual: R$ $BALANCE"

        return 0
    else
        print_error "Falha no depósito"
        echo "$RESPONSE"
        return 1
    fi
}

test_9_withdrawal() {
    print_header "TESTE 9: Transaction Service - Saque"

    # Carrega dados
    if [ -f .test_data ]; then
        source .test_data
    else
        print_error "Execute os testes anteriores primeiro"
        return 1
    fi

    print_info "Testando: POST $GATEWAY_URL/api/transaction/withdrawal"
    print_info "Sacando R$ 200.00 da conta $ACCOUNT_ID"

    RESPONSE=$(curl -s -X POST "$GATEWAY_URL/api/transaction/withdrawal" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $ACCESS_TOKEN" \
        -d "{
            \"source_account_id\": \"$ACCOUNT_ID\",
            \"amount\": 200.00,
            \"description\": \"Saque de teste\"
        }")

    STATUS=$(extract_json_value "$RESPONSE" "status")

    if [ "$STATUS" = "SUCCESS" ]; then
        print_success "Saque realizado com sucesso!"
        sleep 2

        # Verifica saldo
        print_info "Verificando novo saldo..."
        BALANCE=$(curl -s -X GET "$GATEWAY_URL/api/bankaccount/$ACCOUNT_NUMBER/balance" \
            -H "Authorization: Bearer $ACCESS_TOKEN")
        print_info "Saldo atual: R$ $BALANCE (esperado: 800.00)"

        return 0
    else
        print_error "Falha no saque"
        echo "$RESPONSE"
        return 1
    fi
}

test_10_transaction_history() {
    print_header "TESTE 10: Transaction Service - Histórico"

    # Carrega dados
    if [ -f .test_data ]; then
        source .test_data
    else
        print_error "Execute os testes anteriores primeiro"
        return 1
    fi

    print_info "Testando: GET $GATEWAY_URL/api/transaction/account/$ACCOUNT_ID"

    RESPONSE=$(curl -s -X GET "$GATEWAY_URL/api/transaction/account/$ACCOUNT_ID?page=0&size=10" \
        -H "Authorization: Bearer $ACCESS_TOKEN")

    if echo "$RESPONSE" | grep -q "totalElements"; then
        TOTAL=$(echo "$RESPONSE" | grep -o '"totalElements":[0-9]*' | sed 's/"totalElements"://')
        print_success "Histórico encontrado: $TOTAL transações"

        print_info "Transações:"
        echo "$RESPONSE" | grep -o '"transactionType":"[^"]*"' | sed 's/"transactionType":"/  - /' | sed 's/"$//'

        return 0
    else
        print_error "Falha ao consultar histórico"
        echo "$RESPONSE"
        return 1
    fi
}

test_11_transfer() {
    print_header "TESTE 11: Transaction Service - Transferência"

    if [ -f .test_data ]; then
        source .test_data
    else
        print_error "Execute os testes anteriores primeiro"
        return 1
    fi

    if [ -z "$ACCOUNT_ID" ]; then
        print_error "Account ID (origem) não encontrado. Execute o teste 5 ou 6 primeiro."
        return 1
    fi

    # Para o destino, você pode:
    # 1) Criar outra conta e salvar como ACCOUNT_ID_DESTINO, ou
    # 2) Usar a primeira conta da listagem diferente da origem
    # Aqui vou supor que você salvou ACCOUNT_ID_DESTINO em .test_data
    if ! grep -q "ACCOUNT_ID_DESTINO" .test_data; then
        print_error "ACCOUNT_ID_DESTINO não definido. Crie outra conta e salve em .test_data"
        return 1
    fi
    source .test_data

    print_info "Testando: POST $GATEWAY_URL/api/transaction/transfer"
    print_info "Transferindo R$ 250.00 de $ACCOUNT_ID para $ACCOUNT_ID_DESTINO"

    RESPONSE=$(curl -s -X POST "$GATEWAY_URL/api/transaction/transfer" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $ACCESS_TOKEN" \
        -d "{
            \"source_account_id\": \"$ACCOUNT_ID\",
            \"target_account_id\": \"$ACCOUNT_ID_DESTINO\",
            \"amount\": 250.00,
            \"description\": \"Transferência de teste\"
        }")

    STATUS=$(echo "$RESPONSE" | grep -o '"status":"[^"]*"' | sed 's/"status":"\([^"]*\)"/\1/')

    if [ "$STATUS" = "SUCCESS" ]; then
        print_success "Transferência realizada com sucesso!"
        echo "$RESPONSE"
        return 0
    else
        print_error "Falha na transferência"
        echo "$RESPONSE"
        return 1
    fi
}

test_14_create_destination_account_manual() {
    print_header "TESTE 6b: BankAccount - Criar Conta Destinatária (segunda conta)"

    if [ -f .test_data ]; then
        source .test_data
    else
        print_error "Execute os testes anteriores primeiro"
        return 1
    fi

    DEST_ACCOUNT_NUM=$(date +%s | tail -c 10)
    print_info "Testando: POST $GATEWAY_URL/api/bankaccount"
    print_info "Número da conta (destinatária): $DEST_ACCOUNT_NUM"

    RESPONSE=$(curl -s -X POST "$GATEWAY_URL/api/bankaccount" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $ACCESS_TOKEN" \
        -d "{
            \"accountNumber\": \"$DEST_ACCOUNT_NUM\",
            \"agency\": \"0001\",
            \"balance\": 0.00,
            \"accountType\": \"CURRENT\",
            \"userId\": \"$USER_ID\"
        }")

    ACCOUNT_ID_DESTINO=$(echo "$RESPONSE" | grep -o '"id":"[^"]*"' | head -1 | sed 's/"id":"\([^"]*\)"/\1/')

    if [ -n "$ACCOUNT_ID_DESTINO" ]; then
        print_success "Conta destinatária criada com sucesso!"
        print_info "ACCOUNT_ID_DESTINO: $ACCOUNT_ID_DESTINO"
        print_info "ACCOUNT_NUMBER_DESTINO: $DEST_ACCOUNT_NUM"

        # Atualiza arquivo de dados
        sed -i '/ACCOUNT_ID_DESTINO=/d' .test_data 2>/dev/null
        sed -i '/ACCOUNT_NUMBER_DESTINO=/d' .test_data 2>/dev/null
        echo "ACCOUNT_ID_DESTINO=$ACCOUNT_ID_DESTINO" >> .test_data
        echo "ACCOUNT_NUMBER_DESTINO=$DEST_ACCOUNT_NUM" >> .test_data

        return 0
    else
        print_error "Falha ao criar conta destinatária"
        echo "$RESPONSE"
        return 1
    fi
}

test_15_pick_destination_from_list() {
    print_header "TESTE 5b: Selecionar Conta Destinatária da Lista"

    if [ -f .test_data ]; then
        source .test_data
    else
        print_error "Execute os testes anteriores primeiro"
        return 1
    fi

    print_info "Listando contas: GET $GATEWAY_URL/api/bankaccount"
    RESPONSE=$(curl -s -X GET "$GATEWAY_URL/api/bankaccount" \
        -H "Authorization: Bearer $ACCESS_TOKEN")

    # Captura todos IDs e escolhe o primeiro diferente do ACCOUNT_ID atual
    DEST_ID=$(echo "$RESPONSE" | grep -o '"id":"[^"]*"' | sed 's/"id":"\([^"]*\)"/\1/' | grep -v "^$ACCOUNT_ID$" | head -1)
    DEST_NUM=$(echo "$RESPONSE" | grep -o '"accountNumber":"[^"]*"' | sed 's/"accountNumber":"\([^"]*\)"/\1/' | head -1)

    if [ -n "$DEST_ID" ]; then
        print_success "Conta destinatária selecionada!"
        print_info "ACCOUNT_ID_DESTINO: $DEST_ID"
        print_info "ACCOUNT_NUMBER_DESTINO: $DEST_NUM"

        sed -i '/ACCOUNT_ID_DESTINO=/d' .test_data 2>/dev/null
        sed -i '/ACCOUNT_NUMBER_DESTINO=/d' .test_data 2>/dev/null
        echo "ACCOUNT_ID_DESTINO=$DEST_ID" >> .test_data
        echo "ACCOUNT_NUMBER_DESTINO=$DEST_NUM" >> .test_data
        return 0
    else
        print_error "Não encontrei outra conta diferente da origem."
        print_info "Crie uma conta nova com o teste 6b."
        return 1
    fi
}

# ============================================
# MENU INTERATIVO
# ============================================

show_menu() {
    clear
    echo -e "${COLOR_BLUE}"
    echo "╔══════════════════════════════════════════════════╗"
    echo "║                                                  ║"
    echo "║     CapBank - Testes Individuais v1.0           ║"
    echo "║                                                  ║"
    echo "╚══════════════════════════════════════════════════╝"
    echo -e "${COLOR_RESET}"
    echo ""
    echo "Escolha um teste para executar:"
    echo ""
    echo "  Nível 1 - Básico (sem autenticação):"
    echo "    1) Gateway - Health Check"
    echo "    2) User Service - Registro"
    echo "    3) Auth Service - Login"
    echo ""
    echo "  Nível 2 - Com autenticação:"
    echo "    4) User Service - Consultar"
    echo "    5) BankAccount - Listar contas"
    echo "    6) BankAccount - Criar conta manual"
    echo "    7) BankAccount - Consultar saldo"
    echo ""
    echo "  Nível 3 - Transações:"
    echo "    8) Transaction - Depósito"
    echo "    9) Transaction - Saque"
    echo "   10) Transaction - Histórico"
      echo "   11) Transaction - transferência"
    echo ""
    echo "  Outras opções:"
    echo "   12) Executar TODOS os testes sequencialmente"
    echo "   13) Limpar dados de teste"
    echo "   14) BankAccount - Criar Conta Destinatária"
    echo "   15) BankAccount - Selecionar Conta Destinatária da Lista"
    echo "    0) Sair"
    echo ""
    read -p "Digite o número do teste: " choice

    case $choice in
        1) test_1_gateway_health ;;
        2) test_2_user_registration ;;
        3) test_3_user_login ;;
        4) test_4_get_user ;;
        5) test_5_list_accounts ;;
        6) test_6_create_account_manual ;;
        7) test_7_get_balance ;;
        8) test_8_deposit ;;
        9) test_9_withdrawal ;;
        10) test_10_transaction_history ;;
        11) test_11_transfer ;;
        12) run_all_tests ;;
        13) rm -f .test_data && print_success "Dados de teste limpos!" ;;
        14) test_14_create_destination_account_manual ;;
        15) test_15_pick_destination_from_list ;;
        0) exit 0 ;;
        *) print_error "Opção inválida!" ;;
    esac

    echo ""
    read -p "Pressione ENTER para continuar..."
    show_menu
}

run_all_tests() {
    print_header "EXECUTANDO TODOS OS TESTES"

    test_1_gateway_health && \
    test_2_user_registration && \
    test_3_user_login && \
    test_4_get_user && \
    test_5_list_accounts || test_6_create_account_manual && \
    test_7_get_balance && \
    test_8_deposit && \
    test_9_withdrawal && \
    test_10_transaction_history

    print_header "RESULTADO FINAL"
    print_success "Todos os testes foram executados!"
}

# Execução
if [ $# -eq 0 ]; then
    show_menu
else
    # Permite executar teste específico via argumento
    $1
fi
