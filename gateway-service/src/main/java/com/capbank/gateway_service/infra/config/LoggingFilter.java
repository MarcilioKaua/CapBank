package com.capbank.gateway_service.infra.config;

import com.capbank.gateway_service.core.domain.model.GatewayRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class LoggingFilter {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingFilter.class);

    @Bean
    public GlobalFilter globalLoggingFilter() {
        return (exchange, chain) -> {
            GatewayRequestContext context = new GatewayRequestContext("gateway-service");
            exchange.getAttributes().put("gatewayContext", context);

            String path = exchange.getRequest().getURI().getPath();
            exchange.getRequest().getMethod();
            String method = exchange.getRequest().getMethod().name();

            LOG.info("[GATEWAY] Request ID={} | {} {} | started at {}",
                    context.getRequestId(), method, path, context.getTimestamp());

            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> logResponse(exchange, context, path, method)));
        };
    }

    private void logResponse(ServerWebExchange exchange, GatewayRequestContext context, String path, String method) {
        int status = exchange.getResponse().getStatusCode() != null
                ? exchange.getResponse().getStatusCode().value()
                : 0;

        LOG.info("[GATEWAY] Request ID={} | {} {} | completed with status {}",
                context.getRequestId(), method, path, status);
    }
}
