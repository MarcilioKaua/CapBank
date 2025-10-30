package com.capbank.auth_service.infra.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String base64Secret;

    @Value("${jwt.expiration}")
    private long accessExpirationMillis;

    @Value("${jwt.refreshExpiration:604800000}") // default 7 days
    private long refreshExpirationMillis;

    @Value("${jwt.issuer:capbank-gateway}")
    private String issuer;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
    }

    public String generateAccessToken(String cpf) {
        return buildToken(cpf, accessExpirationMillis, "access");
    }

    public String generateRefreshToken(String cpf) {
        return buildToken(cpf, refreshExpirationMillis, "refresh");
    }

    private String buildToken(String subject, long expirationMillis, String tokenType) {
        Instant now = Instant.now();
        SecretKey key = getKey();
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(subject)
                .setIssuer(issuer)
                .claim("tokenType", tokenType)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expirationMillis)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String validateAndGetSubject(String token, String expectedTokenType) {
        Claims claims = parse(token);
        Object type = claims.get("tokenType");
        if (expectedTokenType != null && (type == null || !expectedTokenType.equals(type.toString()))) {
            throw new IllegalArgumentException("Invalid token type");
        }
        return claims.getSubject();
    }

    public long getAccessExpirationSeconds() {
        return accessExpirationMillis / 1000;
    }

    public long getRefreshExpirationSeconds() {
        return refreshExpirationMillis / 1000;
    }
}
