-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS users (
                                     id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                     full_name     VARCHAR(255) NOT NULL,
                                     cpf           VARCHAR(14)  UNIQUE NOT NULL,
                                     email         VARCHAR(255) UNIQUE NOT NULL,
                                     password_hash VARCHAR(255) NOT NULL,
                                     phone         VARCHAR(20),
                                     account_type  VARCHAR(20),
                                     status        VARCHAR(20) DEFAULT 'ACTIVE',
                                     created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);
