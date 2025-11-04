# 🏦 CAP Bank

## 📘 Visão Geral

O **CAP Bank** é um sistema desenvolvido com foco em **operações bancárias internas**, como criação de contas, transferências, consulta de extratos e histórico de transações.  
O sistema inclui um **mecanismo de autenticação robusto** e integração com **mensageria** para envio de notificações (transações, alertas e segurança).

## 🚀 Funcionalidades Principais

- 👤 **Cadastro e autenticação de usuários**
- 💳 **Criação e gerenciamento de contas bancárias**
- 🔄 **Transferências internas entre contas**
- 📄 **Extratos e histórico de transações**
- 🔔 **Notificações automáticas via mensageria**


##  📂 Visão Geral e Arquitetura

### ✅ Executando o Projeto (exemplo)

```bash
# Clonar o repositório
git clone https://github.com/MarcilioKaua/CapBank.git

# Entrar no diretório
cd banco-digital-simplificado

# Rodar com Docker Compose (exemplo)
docker-compose up --build
```

### ⚙️ Tecnologias

| Camada | Sugestão                |
|--------|-------------------------|
| Backend | Java 17 (Spring Boot)   |
| Banco de Dados | PostgreSQL 15           |
| Autenticação | JWT + Refresh Token  |
| Mensageria | RabbitMQ              |
| Frontend | Angular 20              | 
| Infraestrutura | Docker + Docker Compose |
| Testes Unitários | JUnit e Mokito |
| Monitoramento | Prometheus |

### 📚  Arquitetura Adotada por Microsserviço
O sistema implementa a arquitetura Hexagonal (Ports & Adapters), Spring Cloud Gateway (WebFlux) Segurança Centralizada (JWT) e Resiliência (Circuit Breaker).

| Serviço                     | Domínio / Responsabilidade
|:----------------------------|:------------------------------------------------------------------------------------------------|
| **Gateway Service**         | Roteamento, Validação JWT, Autorização                                                          |
| **Auth Service**            | Dominio de autenticação dos usuários Login                                                      |
| **User Service**            | CRUD de usuários                                                                                | 
| **Bank Account Service**    | CRUD de Conta Bancaria                                                                          | 
| **Transaction Service**     | Transações bancárias, Extrato e Histórico, Controle de transação bancária, extrato e histórico  |
| **Notificacao Service**     | Consumo de Eventos e mensageria (RabbitMQ), Envio de E-mail, Log de Persistência.               |

<img width="814" height="804" alt="Microservices Diagram" src="https://github.com/user-attachments/assets/8b2ad338-95b3-4171-bce6-63b450e322af" />

## 🔍 Detalhamento dos Microsserviços e Rotas Chave

| Serviço             | Rotas Chave (Externa)                          | Regra de Autorização Grossa (Gateway) |
|:--------------------|:-----------------------------------------------|:--------------------------------------|
| **Auth Service**         | `POST api/auth/login`                               | **PÚBLICO**                           |
| **User Service**         | `POST api/user/register/`                      | **PÚBLICO**                           |
| **Bank-Account Service** | `POST api/bankaccount/{accountNumber}/balance` | **USER**                              |
| **Transaction Service**  | `POST api/transactions`                         | **USER**                              |

<img width="1092" height="549" alt="Diagrama de Fluxo drawio" src="https://github.com/user-attachments/assets/bd1e05b5-a3da-4da1-b42e-d103c81bba77" />


## 🧑‍💻 Infraestrutura e DevOps

### Docker Compose e Isolamento

* **Rede:** Todos os MS, o RabbitMQ e o Postgres residem na rede privada (`capbank-network`). A comunicação é feita via **nome do serviço** (`http://gateway-service:8081`).
* **Exposição:** Apenas o **API Gateway** expõe a porta `8081`.
* **DB Isolados:** Utilizamos um container **Postgres** para cada MS, com três bases de dados lógicas e isoladas (`user_db`, `db_transaction`, `db_bankaccount`) — o padrão *Database per Service*.

## 🧩 Arquitetura de Entidades
### 1. 🧍 Usuário (`User`)
Representa a pessoa que utiliza o sistema.

| Campo | Tipo | Descrição |
|-------|------|------------|
| id | UUID | Identificador único |
| nome_completo | String | Nome do usuário |
| cpf | String | Documento único |
| email | String | Login do usuário |
| senha_hash | String | Senha criptografada |
| phone | String | Contato do usuário |
| status | Enum | ativo, bloqueado |
| data_criacao | DateTime | Data de cadastro |

---

### 2. 🏦 Conta (`Account`)
Armazena informações da conta bancária do usuário.

| Campo | Tipo | Descrição |
|-------|------|------------|
| id | UUID | Identificador da conta |
| numero_conta | String | Número único |
| agencia | String | Agência vinculada |
| saldo | Decimal | Saldo atual |
| tipo_conta | Enum | corrente, poupança, digital |
| usuario_id | UUID | Dono da conta |
| status | Enum | ativa, bloqueada, encerrada |
| data_abertura | DateTime | Data de criação |

---

### 3. 💰 Transação (`Transaction`)
Registra movimentações financeiras entre contas.

| Campo | Tipo | Descrição |
|-------|------|------------|
| id | UUID | Identificador da transação |
| conta_origem_id | UUID | Conta que envia (pode ser null) |
| conta_destino_id | UUID | Conta que recebe (pode ser null) |
| tipo_transacao | Enum | depósito, saque, transferência |
| valor | Decimal | Valor movimentado |
| data_transacao | DateTime | Data/hora da operação |
| status | Enum | sucesso, pendente, falha |
| descricao | String | Observação opcional |

---

### 4. 📜 Histórico / Extrato (`TransactionHistory`)
Mantém registro imutável de movimentações e saldos.

| Campo | Tipo | Descrição |
|-------|------|------------|
| id | UUID | Identificador do registro |
| conta_id | UUID | Conta relacionada |
| transacao_id | UUID | Transação correspondente |
| saldo_antes | Decimal | Saldo anterior |
| saldo_depois | Decimal | Saldo posterior |
| descricao | String | Detalhe da operação |
| data_registro | DateTime | Data da atualização |

---
### 5. 💬 Notificação (`Notification`)
Gerencia mensagens automáticas enviadas ao usuário.

| Campo | Tipo | Descrição |
|-------|------|------------|
| id | UUID | Identificador |
| usuario_id | UUID | Usuário destinatário |
| tipo | Enum | transação, alerta, segurança |
| titulo | String | Título da notificação |
| mensagem | String | Conteúdo |
| canal | Enum | email, sms, push |
| status_envio | Enum | pendente, enviado, erro |
| data_criacao | DateTime | Geração |
| data_envio | DateTime | Envio efetivo |

### 🔗 Relacionamentos (Resumo)

| Entidade | Relacionamento | Com |
|-----------|----------------|-----|
| Usuário | 1:N | Conta |
| Conta | 1:N | Transação (origem/destino) |
| Conta | 1:N | Histórico |
| Usuário | 1:N | Notificação |
| Transação | 1:N | Notificação (via evento) |

## Mensageria

### 📩 Fila de Mensagens (`MessageQueue`) -- Não persiste no banco
Simula ou integra com um sistema de mensageria (RabbitMQ, Kafka, etc.).

| Campo | Tipo | Descrição |
|-------|------|------------|
| id | UUID | Identificador |
| tipo_evento | String | Tipo do evento (NEW_TRANSACTION, ALERT, etc.) |
| payload | JSON | Dados da mensagem |
| status | Enum | pendente, processado |
| data_criacao | DateTime | Criação |
| data_processamento | DateTime | Processamento |

---
## Principais Telas do sistema
### Login
<img width="1989" height="1723" alt="Frame 1" src="https://github.com/user-attachments/assets/eb7f7a90-9aa6-405a-9aed-683d70d16b5f" />

### Cadastrar usuário
<img width="1891" height="1020" alt="Group 2" src="https://github.com/user-attachments/assets/0a2a6d10-6ccf-4e4a-a3db-4cbbb52be41e" />

### Extrato
<img width="1971" height="961" alt="Group 3" src="https://github.com/user-attachments/assets/752e1e0d-33b6-4241-86d9-c3e62af3ef97" />

### Transferência
<img width="1863" height="1440" alt="Group 4" src="https://github.com/user-attachments/assets/939fcd09-367b-445b-b302-79cf6f4d9679" />

---
## 🧠 Extensões Futuras

### Funcionalidades
- 💳 Módulo de Cartões (crédito/débito virtuais)
- 💡 Chaves PIX e QR Code dinâmico
- 📈 Relatórios e dashboards de movimentação
- 🕵️ Logs de auditoria detalhados

### 🔐 Sessão / Token (`AuthSession`)
Controla sessões de autenticação e tokens JWT/refresh.

| Campo | Tipo | Descrição |
|-------|------|------------|
| id | UUID | Identificador |
| usuario_id | UUID | Usuário autenticado |
| token | String | Token de sessão |
| data_criacao | DateTime | Criação da sessão |
| data_expiracao | DateTime | Expiração do token |
| ip_origem | String | Endereço IP |
| user_agent | String | Dispositivo/navegador |

---

### 🧾 Log de Acesso (`AccessLog`)
Rastreia tentativas de login e acessos para auditoria.

| Campo | Tipo | Descrição |
|-------|------|------------|
| id | UUID | Identificador |
| usuario_id | UUID | Usuário relacionado |
| data_hora | DateTime | Momento do acesso |
| resultado | Enum | sucesso, falha |
| ip | String | IP de origem |
| mensagem | String | Mensagem informativa |

