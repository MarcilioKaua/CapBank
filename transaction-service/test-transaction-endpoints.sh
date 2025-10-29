
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' 


BASE_URL="${1:-http://localhost:8085}"
API_PATH="/api/transactions"
FULL_URL="${BASE_URL}${API_PATH}"


TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

print_header() {
    echo -e "${BLUE}================================================${NC}"
    echo -e "${BLUE}  CapBank - Transaction Service Tests${NC}"
    echo -e "${BLUE}================================================${NC}"
    echo -e "Base URL: ${YELLOW}${BASE_URL}${NC}"
    echo -e "Testing endpoints: ${YELLOW}${API_PATH}${NC}"
    echo ""
}

print_test_result() {
    local test_name=$1
    local status=$2
    local details=$3

    ((TOTAL_TESTS++))

    if [ "$status" == "PASS" ]; then
        echo -e "${GREEN}✓ PASS${NC}: $test_name"
        ((PASSED_TESTS++))
    else
        echo -e "${RED}✗ FAIL${NC}: $test_name"
        echo -e "${RED}  Details: $details${NC}"
        ((FAILED_TESTS++))
    fi
}


check_service() {
    echo -e "${YELLOW}Checking if service is running...${NC}"

    
    local response=$(curl -s -o /dev/null -w "%{http_code}" --connect-timeout 5 "${FULL_URL}" 2>/dev/null)

   
    if [ ! -z "$response" ] && [ "$response" != "000" ]; then
        echo -e "${GREEN}✓ Service is running (HTTP $response)${NC}"
        echo ""
        return 0
    else
        echo -e "${RED}✗ Service is not responding at ${BASE_URL}${NC}"
        echo -e "${YELLOW}Tip: Make sure the transaction-service is running on port 8085${NC}"
        echo -e "${YELLOW}You can still run the tests - they will fail if the service is down${NC}"
        echo ""
        read -p "Continue anyway? (y/n) " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 1
        fi
    fi
}


test_deposit() {
    echo -e "${BLUE}--- Testing DEPOSIT endpoint ---${NC}"

    
    local response=$(curl -s -w "\n%{http_code}" -X POST "${FULL_URL}/deposit" \
        -H "Content-Type: application/json" \
        -d '{
            "target_account_id": "550e8400-e29b-41d4-a716-446655440001",
            "amount": 100.00,
            "description": "Test deposit from script"
        }')

    local http_code=$(echo "$response" | tail -n1)
    local body=$(echo "$response" | sed '$d')

    if [ "$http_code" == "201" ]; then
        if echo "$body" | grep -q "DEPOSIT"; then
            print_test_result "POST /deposit - Valid deposit" "PASS"
        else
            print_test_result "POST /deposit - Valid deposit" "FAIL" "Response body does not contain DEPOSIT"
        fi
    else
        print_test_result "POST /deposit - Valid deposit" "FAIL" "Expected 201, got $http_code"
    fi

    response=$(curl -s -w "\n%{http_code}" -X POST "${FULL_URL}/deposit" \
        -H "Content-Type: application/json" \
        -d '{
            "amount": 100.00,
            "description": "Invalid deposit"
        }')

    http_code=$(echo "$response" | tail -n1)

    if [ "$http_code" == "400" ]; then
        print_test_result "POST /deposit - Missing target_account_id" "PASS"
    else
        print_test_result "POST /deposit - Missing target_account_id" "FAIL" "Expected 400, got $http_code"
    fi

    response=$(curl -s -w "\n%{http_code}" -X POST "${FULL_URL}/deposit" \
        -H "Content-Type: application/json" \
        -d '{
            "target_account_id": "550e8400-e29b-41d4-a716-446655440001",
            "amount": -50.00,
            "description": "Invalid deposit"
        }')

    http_code=$(echo "$response" | tail -n1)

    if [ "$http_code" == "400" ]; then
        print_test_result "POST /deposit - Negative amount" "PASS"
    else
        print_test_result "POST /deposit - Negative amount" "FAIL" "Expected 400, got $http_code"
    fi

    echo ""
}


test_withdrawal() {
    echo -e "${BLUE}--- Testing WITHDRAWAL endpoint ---${NC}"

   
    local response=$(curl -s -w "\n%{http_code}" -X POST "${FULL_URL}/withdrawal" \
        -H "Content-Type: application/json" \
        -d '{
            "source_account_id": "550e8400-e29b-41d4-a716-446655440000",
            "amount": 50.00,
            "description": "Test withdrawal from script"
        }')

    local http_code=$(echo "$response" | tail -n1)
    local body=$(echo "$response" | sed '$d')

    if [ "$http_code" == "201" ]; then
        if echo "$body" | grep -q "WITHDRAWAL"; then
            print_test_result "POST /withdrawal - Valid withdrawal" "PASS"
        else
            print_test_result "POST /withdrawal - Valid withdrawal" "FAIL" "Response body does not contain WITHDRAWAL"
        fi
    else
        print_test_result "POST /withdrawal - Valid withdrawal" "FAIL" "Expected 201, got $http_code"
    fi

    
    response=$(curl -s -w "\n%{http_code}" -X POST "${FULL_URL}/withdrawal" \
        -H "Content-Type: application/json" \
        -d '{
            "amount": 50.00,
            "description": "Invalid withdrawal"
        }')

    http_code=$(echo "$response" | tail -n1)

    if [ "$http_code" == "400" ]; then
        print_test_result "POST /withdrawal - Missing source_account_id" "PASS"
    else
        print_test_result "POST /withdrawal - Missing source_account_id" "FAIL" "Expected 400, got $http_code"
    fi

    response=$(curl -s -w "\n%{http_code}" -X POST "${FULL_URL}/withdrawal" \
        -H "Content-Type: application/json" \
        -d '{
            "source_account_id": "550e8400-e29b-41d4-a716-446655440000",
            "amount": 0,
            "description": "Invalid withdrawal"
        }')

    http_code=$(echo "$response" | tail -n1)

    if [ "$http_code" == "400" ]; then
        print_test_result "POST /withdrawal - Zero amount" "PASS"
    else
        print_test_result "POST /withdrawal - Zero amount" "FAIL" "Expected 400, got $http_code"
    fi

    echo ""
}

test_transfer() {
    echo -e "${BLUE}--- Testing TRANSFER endpoint ---${NC}"

    local response=$(curl -s -w "\n%{http_code}" -X POST "${FULL_URL}/transfer" \
        -H "Content-Type: application/json" \
        -d '{
            "source_account_id": "550e8400-e29b-41d4-a716-446655440000",
            "target_account_id": "550e8400-e29b-41d4-a716-446655440001",
            "amount": 75.00,
            "description": "Test transfer from script"
        }')

    local http_code=$(echo "$response" | tail -n1)
    local body=$(echo "$response" | sed '$d')

    if [ "$http_code" == "201" ]; then
        if echo "$body" | grep -q "TRANSFER"; then
            print_test_result "POST /transfer - Valid transfer" "PASS"
        else
            print_test_result "POST /transfer - Valid transfer" "FAIL" "Response body does not contain TRANSFER"
        fi
    else
        print_test_result "POST /transfer - Valid transfer" "FAIL" "Expected 201, got $http_code"
    fi

    response=$(curl -s -w "\n%{http_code}" -X POST "${FULL_URL}/transfer" \
        -H "Content-Type: application/json" \
        -d '{
            "source_account_id": "550e8400-e29b-41d4-a716-446655440000",
            "target_account_id": "550e8400-e29b-41d4-a716-446655440000",
            "amount": 75.00,
            "description": "Invalid transfer"
        }')

    http_code=$(echo "$response" | tail -n1)

    if [ "$http_code" == "400" ]; then
        print_test_result "POST /transfer - Same source and target" "PASS"
    else
        print_test_result "POST /transfer - Same source and target" "FAIL" "Expected 400, got $http_code"
    fi

    response=$(curl -s -w "\n%{http_code}" -X POST "${FULL_URL}/transfer" \
        -H "Content-Type: application/json" \
        -d '{
            "source_account_id": "550e8400-e29b-41d4-a716-446655440000",
            "amount": 75.00,
            "description": "Invalid transfer"
        }')

    http_code=$(echo "$response" | tail -n1)

    if [ "$http_code" == "400" ]; then
        print_test_result "POST /transfer - Missing target_account_id" "PASS"
    else
        print_test_result "POST /transfer - Missing target_account_id" "FAIL" "Expected 400, got $http_code"
    fi

    response=$(curl -s -w "\n%{http_code}" -X POST "${FULL_URL}/transfer" \
        -H "Content-Type: application/json" \
        -d '{
            "target_account_id": "550e8400-e29b-41d4-a716-446655440001",
            "amount": 75.00,
            "description": "Invalid transfer"
        }')

    http_code=$(echo "$response" | tail -n1)

    if [ "$http_code" == "400" ]; then
        print_test_result "POST /transfer - Missing source_account_id" "PASS"
    else
        print_test_result "POST /transfer - Missing source_account_id" "FAIL" "Expected 400, got $http_code"
    fi

    echo ""
}


print_summary() {
    echo -e "${BLUE}================================================${NC}"
    echo -e "${BLUE}  Test Summary${NC}"
    echo -e "${BLUE}================================================${NC}"
    echo -e "Total Tests: ${YELLOW}${TOTAL_TESTS}${NC}"
    echo -e "Passed: ${GREEN}${PASSED_TESTS}${NC}"
    echo -e "Failed: ${RED}${FAILED_TESTS}${NC}"

    if [ $FAILED_TESTS -eq 0 ]; then
        echo -e "\n${GREEN}🎉 All tests passed!${NC}"
        exit 0
    else
        echo -e "\n${RED}❌ Some tests failed.${NC}"
        exit 1
    fi
}


main() {
    print_header
    check_service
    test_deposit
    test_withdrawal
    test_transfer
    print_summary
}


main
