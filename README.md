# 🏦 Banco Digital Simplificado

## 📘 Visão Geral

O **Banco Digital Simplificado** é um sistema desenvolvido com foco em **operações bancárias internas**, como criação de contas, transferências, consulta de extratos e histórico de transações.  
O sistema inclui um **mecanismo de autenticação robusto** e integração com **mensageria** para envio de notificações (transações, alertas e segurança).

## 🚀 Funcionalidades Principais

- 👤 **Cadastro e autenticação de usuários**
- 💳 **Criação e gerenciamento de contas bancárias**
- 🔄 **Transferências internas entre contas**
- 📄 **Extratos e histórico de transações**
- 🔔 **Notificações automáticas via mensageria**


##  📂 Visão Geral e Arquitetura

### ⚙️ Tecnologias Recomendadas

| Camada | Sugestão                |
|--------|-------------------------|
| Backend | Java 17 (Spring Boot)   |
| Banco de Dados | PostgreSQL 15           |
| Autenticação | JWT + Refresh Token     |
| Mensageria | RabbitMQ                |
| Frontend | Angular 20              | 
| Infraestrutura | Docker + Docker Compose |

### 📚  Arquitetura Adotada por Microsserviço
O sistema implementa a arquitetura Hexagonal (Ports & Adapters), Segurança Centralizada (JWT) e Resiliência (Circuit Breaker).

| Serviço              | Domínio / Responsabilidade                                | Padrão Arquitetural | Justificativa                                                                |
|:---------------------|:----------------------------------------------------------| :--- |:-----------------------------------------------------------------------------|
| **API Gateway**      | Roteamento, Validação JWT, Autorização Grossa.            | Spring Cloud Gateway (WebFlux) | Ponto de entrada e motor de segurança reativo.                               |
| **Auth MS**          | Identidade, Login                                         | **Hexagonal (Ports & Adapters)** | Dominio de autenticação dos usuários                                         |
| **User MS**          | Identidade, Cadastro de usuários                          | **Hexagonal (Ports & Adapters)** | Domínio de dados simples (CRUD).                                             |
| **Banck-account MS** | CRUD de Conta Bancaria                                    | **Hexagonal (Ports & Adapters)** | Domínio de dados simples (CRUD).                                             |
| **Transaction MS**   | Transações bancárias, Extrato e Histórico                 | **Hexagonal (Ports & Adapters)** | Domínio de dados de todo controle de transação bancária, extrato e histórico |
| **Notificacao MS**   | Consumo de Eventos, Envio de E-mail, Log de Persistência. | **Hexagonal (Ports & Adapters)** | Isolamento de infraestrutura de mensageria (RabbitMQ).                       |


## 🔍 Detalhamento dos Microsserviços e Rotas Chave

| Serviço             | Rotas Chave (Externa)                          | Regra de Autorização Grossa (Gateway) |
|:--------------------|:-----------------------------------------------|:--------------------------------------|
| **Auth MS**         | `api/auth/login`                               | **PÚBLICO**                           |
| **User MS**         | `POST api/user/register/`                      | **PÚBLICO**                           |
| **Bank-Account MS** | `POST api/bankaccount/{accountNumber}/balance` | **USER**                              |
| **Transaction MS**  | `GET api/transactions`                         | **USER**                              |

## 🧑‍💻 Infraestrutura e DevOps

### Docker Compose e Isolamento

* **Rede:** Todos os MS, o RabbitMQ e o Postgres residem na rede privada (`capbank-network`). A comunicação é feita via **nome do serviço** (`http://auth-service:8081`).
* **Exposição:** Apenas o **API Gateway** expõe a porta `8081`.
* **DB Isolados:** Utilizamos um container **Postgres** para cada MS, com três bases de dados lógicas e isoladas (`user_db`, `db_transactions`, `db_bankaccount`) — o padrão *Database per Service*.

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
<img width="1200" height="1200" alt="image" src="https://github.com/user-attachments/assets/82e32a34-bc79-4b57-ad08-226efe898aa8" />

### Cadastrar usuário
<img width="1200" height="1200" alt="image" src="https://github.com/user-attachments/assets/11457c99-9159-456a-9339-a184090957b8" />

### Extrato
<img width="1200" height="1200" alt="image" src="https://github.com/user-attachments/assets/c946185f-b90d-4d09-a0bd-2a0063dc051b" />

### Transferência
<img width="3726" height="2880" alt="image" src="https://github.com/user-attachments/assets/add2577b-2a4a-4234-b7fc-1d72a9a7d372" />


## 🧪 Executando o Projeto (exemplo)

```bash
# Clonar o repositório
git clone https://github.com/MarcilioKaua/CapBank.git

# Entrar no diretório
cd banco-digital-simplificado

# Rodar com Docker Compose (exemplo)
docker-compose up --build
```

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

