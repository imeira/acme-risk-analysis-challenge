package com.acme.risk.analysis.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utilitário para geração e validação de tokens JWT.
 */
@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret:acme-risk-analysis-secret-key-for-internal-communication}")
    private String secretKey;

    @Value("${jwt.expiration:3600000}") // 1 hora em millisegundos
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Gera um token JWT para comunicação entre serviços.
     * 
     * @param serviceName nome do serviço emissor
     * @return token JWT
     */
    public String generateServiceToken(String serviceName) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject("service-communication")
                .setIssuer(serviceName)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("service", serviceName)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida um token JWT.
     * 
     * @param token token a ser validado
     * @return true se o token for válido, false caso contrário
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("Token JWT inválido: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extrai as claims de um token JWT.
     * 
     * @param token token JWT
     * @return claims do token
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Verifica se um token está expirado.
     * 
     * @param token token JWT
     * @return true se o token estiver expirado, false caso contrário
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}

