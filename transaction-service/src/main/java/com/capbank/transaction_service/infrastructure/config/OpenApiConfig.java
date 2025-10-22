package com.capbank.transaction_service.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração customizada para OpenAPI/Swagger
 * Define informações detalhadas sobre a API e seus endpoints
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Transaction History Service API")
                        .version("1.0.0")
                        .description("""
                                ## Microserviço de Histórico de Transações

                                Esta API fornece endpoints para gerenciar o histórico de transações bancárias.

                                ### Funcionalidades principais:
                                - ✅ **Criação de histórico**: Registra novas movimentações
                                - ✅ **Consulta por ID**: Busca registros específicos
                                - ✅ **Consulta por conta**: Lista histórico com filtros e paginação

                                ### Arquitetura:
                                - **Padrão**: Hexagonal (Ports & Adapters)
                                - **Tecnologias**: Spring Boot 3, PostgreSQL, JPA
                                - **Validações**: Bean Validation com tratamento global de erros

                                ### Filtros disponíveis:
                                - Tipo de transação (DEPOSIT, WITHDRAWAL, TRANSFER)
                                - Período de datas
                                - Paginação e ordenação
                                """)
                        .contact(new Contact()
                                .name("Capbank Team")
                                .email("team@capbank.com")
                                .url("https://github.com/capbank"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8083/transaction-service")
                                .description("Ambiente de Desenvolvimento"),
                        new Server()
                                .url("https://api.capbank.com/transaction-service")
                                .description("Ambiente de Produção")
                ));
    }
}