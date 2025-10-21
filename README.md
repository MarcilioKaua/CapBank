# 🏦 Banco Digital Simplificado

## 📘 Visão Geral

O **Banco Digital Simplificado** é um sistema desenvolvido com foco em **operações bancárias internas**, como criação de contas, transferências, consulta de extratos e histórico de transações.  
O sistema inclui um **mecanismo de autenticação robusto** e integração com **mensageria** para envio de notificações (transações, alertas e segurança).


## Figma: 

https://www.figma.com/design/Q1biPUzijosAzFwfc4dkOE/Projeto-Final--Grupo-01----BANCO-DIGITAL?node-id=1-2&t=wptjOJs6ey3P1QJ9-0

## Front:
 - Alison (Figma)
 - Juliana (Figma)
 - Carla (Figma)

## Backend: 
 - Gustavo (MS Usuario Autenticação) 
 - Marcilio (MS Conta de Usuario, MS Transferencia, Docker)
 - Juliana (Autenticação, Gateway)
 - Carla (Extrato)
 - Alison (Notificação)

 ### Mensageria (Envio dos emails)
 RabbitMQ - 

## Nomeclarutas


---

## 🚀 Funcionalidades Principais

- 👤 **Cadastro e autenticação de usuários**
- 💳 **Criação e gerenciamento de contas bancárias**
- 🔄 **Transferências internas entre contas**
- 📄 **Extratos e histórico de transações**
- 🔔 **Notificações automáticas via mensageria**

---

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
| telefone | String | Contato do usuário |
| status | Enum | ativo, bloqueado |
| data_criacao | DateTime | Data de cadastro |

**Relacionamentos:**
- 1 Usuário → N Contas
- 1 Usuário → N Notificações
- 1 Usuário → N Sessões de login

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

**Relacionamentos:**
- 1 Conta → N Transações
- 1 Conta → N Registros de Histórico

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

**Relacionamentos:**
- 1 Transação → gera 1 ou mais notificações

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


## 🔗 Relacionamentos (Resumo)

| Entidade | Relacionamento | Com |
|-----------|----------------|-----|
| Usuário | 1:N | Conta |
| Conta | 1:N | Transação (origem/destino) |
| Conta | 1:N | Histórico |
| Usuário | 1:N | Notificação |
| Transação | 1:N | Notificação (via evento) |
---

# Mensageria

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


---

## 🧠 Extensões Futuras

- 💳 Módulo de Cartões (crédito/débito virtuais)  
- 💡 Chaves PIX e QR Code dinâmico  
- 📈 Relatórios e dashboards de movimentação  
- 🕵️ Logs de auditoria detalhados  

---

## ⚙️ Tecnologias Recomendadas

| Camada | Sugestão |
|--------|-----------|
| Backend | Java 17 (Spring Boot) |
| Banco de Dados | PostgreSQL |
| Autenticação | JWT + Refresh Token |
| Mensageria | RabbitMQ |
| Frontend | Angular 20|
| Infraestrutura | Docker + Docker Compose |

---

## 🧪 Executando o Projeto (exemplo)

```bash
# Clonar o repositório
git clone https://github.com/seuusuario/banco-digital-simplificado.git

# Entrar no diretório
cd banco-digital-simplificado

# Rodar com Docker Compose (exemplo)
docker-compose up --build

```


# Backlog
### 5. 🔐 Sessão / Token (`AuthSession`)
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

### 6. 🧾 Log de Acesso (`AccessLog`)
Rastreia tentativas de login e acessos para auditoria.

| Campo | Tipo | Descrição |
|-------|------|------------|
| id | UUID | Identificador |
| usuario_id | UUID | Usuário relacionado |
| data_hora | DateTime | Momento do acesso |
| resultado | Enum | sucesso, falha |
| ip | String | IP de origem |
| mensagem | String | Mensagem informativa |

