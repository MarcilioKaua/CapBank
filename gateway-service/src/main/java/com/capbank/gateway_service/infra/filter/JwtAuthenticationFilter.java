package com.capbank.gateway_service.infra.filter;

import com.capbank.gateway_service.core.application.ports.out.AuthClientPort;
import com.capbank.gateway_service.infra.config.JwtService;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final String AUTH_HEADER = "Authorization";
    private static final String REFRESH_HEADER = "Refresh-Token";

    private final JwtService jwtService;
    private final AuthClientPort authClient;

    public JwtAuthenticationFilter(JwtService jwtService, AuthClientPort authClient) {
        this.jwtService = jwtService;
        this.authClient = authClient;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();

        if (isPublicRoute(path)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(AUTH_HEADER);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            LOG.warn("Missing or invalid Authorization header for path {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtService.validateToken(token);

            LOG.info("JWT validated for subject={} at path={}", safeHash(claims.getSubject()), path);

            return chain.filter(exchange);

        } catch (Exception e) {
            LOG.warn("JWT validation failed at path {}: {}", path, e.getMessage());

            String methodName = request.getMethod() != null ? request.getMethod().name() : "";
            if (isTransactionOrTransferRequest(path, methodName)) {
                String refreshToken = request.getHeaders().getFirst(REFRESH_HEADER);
                if (refreshToken != null && !refreshToken.isBlank()) {
                    try {
                        String newAccess = authClient.refreshAccessToken(refreshToken);
                        LOG.info("Access token refreshed successfully for path {}", path);
                        ServerHttpRequest mutated = new ServerHttpRequestDecorator(request) {
                            @Override
                            public org.springframework.http.HttpHeaders getHeaders() {
                                org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                                headers.putAll(super.getHeaders());
                                headers.set(AUTH_HEADER, "Bearer " + newAccess);
                                return headers;
                            }
                        };
                        return chain.filter(exchange.mutate().request(mutated).build());
                    } catch (Exception ex) {
                        LOG.warn("Refresh attempt failed for path {}: {}", path, ex.getMessage());
                    }
                } else {
                    LOG.debug("No refresh token provided in header {} for path {}", REFRESH_HEADER, path);
                }
            }

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isPublicRoute(String path) {
        return path.startsWith("/api/auth")
                || path.startsWith("/api/user")
                || path.startsWith("/api/gateway/user-registered")
                || path.startsWith("/api/gateway/user-logged")
                || path.startsWith("/actuator")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/api-docs")
                || path.startsWith("/api/auth/refresh");
    }

    private boolean isTransactionOrTransferRequest(String path, String method) {
        if (!"POST".equalsIgnoreCase(method)) return false;
        return path.startsWith("/api/transactions");
    }

    private String safeHash(String value) {
        return Integer.toHexString(value == null ? 0 : value.hashCode());
    }
}
