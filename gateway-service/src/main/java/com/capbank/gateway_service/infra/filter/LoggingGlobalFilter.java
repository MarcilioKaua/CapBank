package com.capbank.gateway_service.infra.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(LoggingGlobalFilter.class);

    private static final String REQ_ID_ATTR = "LOG_REQUEST_ID";
    private static final String START_TIME_ATTR = "LOG_START_TIME";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        long start = System.currentTimeMillis();
        exchange.getAttributes().put(START_TIME_ATTR, start);

        String correlationId = exchange.getRequest().getHeaders().getFirst("X-Request-Id");
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        exchange.getResponse().getHeaders().set("X-Request-Id", correlationId);
        exchange.getAttributes().put(REQ_ID_ATTR, correlationId);

        logRequest(exchange, correlationId);

        final ServerWebExchange ex = exchange;
        final String cid = correlationId;
        return chain.filter(exchange)
                .then(Mono.defer(() ->
                        ReactiveSecurityContextHolder.getContext()
                                .map(SecurityContext::getAuthentication)
                                .doOnNext(auth -> logResponse(ex, cid, auth))
                                .switchIfEmpty(Mono.defer(() -> {
                                    logResponse(ex, cid, null);
                                    return Mono.empty();
                                }))
                                .then()
                ));
    }

    private void logRequest(ServerWebExchange exchange, String correlationId) {
        ServerHttpRequest request = exchange.getRequest();
        request.getMethod();
        String method = request.getMethod().name();
        String path = request.getURI().getRawPath();
        String query = request.getURI().getRawQuery();

        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String routeId = route != null ? route.getId() : "unknown-route";
        URI targetUri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);

        List<String> headers = request.getHeaders().entrySet().stream()
                .filter(e -> !e.getKey().equalsIgnoreCase("Authorization"))
                .map(e -> e.getKey() + "=" + String.join(",", e.getValue()))
                .collect(Collectors.toList());

        log.info("[GW][REQ][{}][{}] route={} method={} path={}{} target={} headers=[{}] at={}",
                correlationId,
                moduleFromRoute(routeId),
                routeId,
                method,
                path,
                (query != null && !query.isBlank() ? ("?" + query) : ""),
                (targetUri != null ? targetUri : ""),
                String.join("; ", headers),
                Instant.now());
    }

    private void logResponse(ServerWebExchange exchange, String correlationId, Authentication authentication) {
        Integer status = exchange.getResponse().getStatusCode() != null ? exchange.getResponse().getStatusCode().value() : null;
        long start = exchange.getAttributeOrDefault(START_TIME_ATTR, System.currentTimeMillis());
        long durationMs = System.currentTimeMillis() - start;

        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String routeId = route != null ? route.getId() : "unknown-route";

        String username = null;
        String role = null;
        if (authentication != null) {
            username = authentication.getName();
            role = authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()
                    ? authentication.getAuthorities().iterator().next().getAuthority()
                    : null;
        }

        log.info("[GW][RES][{}][{}] route={} status={} durationMs={} user={} role={} at={}",
                correlationId,
                moduleFromRoute(routeId),
                routeId,
                status,
                durationMs,
                safe(username),
                safe(role),
                Instant.now());
    }

    private String moduleFromRoute(String routeId) {
        if (routeId == null) return "unknown";
        if (routeId.contains("user-service")) return "user-service";
        if (routeId.contains("auth-service")) return "auth-service";
        if (routeId.contains("account-service")) return "account-service";
        if (routeId.contains("transaction-service")) return "transaction-service";
        return routeId;
    }

    private String safe(String v) { return v == null ? "-" : v; }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
