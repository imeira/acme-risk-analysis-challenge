package com.acme.lists.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utilitário para validação de tokens JWT.
 */
@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret:acme-risk-analysis-secret-key-for-internal-communication}")
    private String secretKey;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Valida um token JWT.
     * 
     * @param token token a ser validado
     * @return true se o token for válido, false caso contrário
     */
    public boolean validateToken(String token) {
        try {
            log.debug("Validating JWT token: {}", token);
            log.debug("Signing key being used: {}", secretKey);
            
            // Split the token to log its parts
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                log.error("Invalid JWT token format. Expected 3 parts but got {}", parts.length);
                return false;
            }
            
            log.debug("JWT Header: {}", new String(java.util.Base64.getUrlDecoder().decode(parts[0])));
            log.debug("JWT Payload: {}", new String(java.util.Base64.getUrlDecoder().decode(parts[1])));
            
            // Validate the token
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
                
            log.debug("Token validation successful. Claims: {}", claims);
            return true;
            
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error validating JWT token: {}", e.getMessage(), e);
        }
        
        return false;
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
            log.debug("Checking if token is expired");
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            Date now = new Date();
            
            log.debug("Token expiration: {}", expiration);
            log.debug("Current time: {}", now);
            log.debug("Is token expired? {}", expiration.before(now));
            
            return expiration.before(now);
            
        } catch (Exception e) {
            log.error("Error checking if token is expired: {}", e.getMessage());
            return true; // If there's an error, consider the token as expired
        }
    }
}

