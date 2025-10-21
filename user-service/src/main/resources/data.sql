-- ========================================
-- DATA.SQL — Inserção inicial de usuários
-- ========================================

INSERT INTO users (nome_completo, cpf, email, senha_hash, telefone, status)
VALUES
    ('Administrador CapBank', '000.000.000-00', 'admin@capbank.com', '$2a$10$qLrjZKqmE3hzdtA4gY63tuUp0Ugm3cFwnQK4Sn8C/EbY4b3ugMGLe', '(11) 99999-0000', 'ATIVO'),
    ('Cliente Teste', '111.111.111-11', 'cliente@capbank.com', '$2a$10$0P9E5Tt1Fd4O8v7PDbMJuux2O5Mtz8a7u.2hiZSv4Aatn1pbWddC.', '(11) 98888-1111', 'ATIVO');
