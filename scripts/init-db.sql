
CREATE SCHEMA IF NOT EXISTS transaction_history;

ALTER DATABASE db_transactions SET search_path TO transaction_history, public;

-- Garantir permissões para o usuário postgres
GRANT ALL PRIVILEGES ON SCHEMA transaction_history TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA transaction_history TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA transaction_history TO postgres;

-- Configurações de performance
ALTER SYSTEM SET shared_preload_libraries = 'pg_stat_statements';
ALTER SYSTEM SET max_connections = 100;
ALTER SYSTEM SET shared_buffers = '256MB';
ALTER SYSTEM SET effective_cache_size = '1GB';
ALTER SYSTEM SET maintenance_work_mem = '64MB';
ALTER SYSTEM SET checkpoint_completion_target = 0.9;
ALTER SYSTEM SET wal_buffers = '16MB';
ALTER SYSTEM SET default_statistics_target = 100;

-- Criar uma tabela de log de inicialização
CREATE TABLE IF NOT EXISTS initialization_log (
    id SERIAL PRIMARY KEY,
    event VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO initialization_log (event) VALUES ('Database initialized successfully');


SELECT version() as postgresql_version;
SELECT current_database() as current_db;
SELECT current_user as current_user;
SELECT inet_server_addr() as server_address;
SELECT inet_server_port() as server_port;