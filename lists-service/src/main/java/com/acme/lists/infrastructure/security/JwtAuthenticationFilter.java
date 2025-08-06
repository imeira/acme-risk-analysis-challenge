package com.acme.lists.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Filtro de autenticação JWT para validar tokens nas requisições.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        
        // Permitir acesso ao health check sem autenticação
        if (path.contains("/health")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            
            try {
                if (jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
                    // Token válido - criar autenticação
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken("service", null, new ArrayList<>());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    log.debug("Token JWT válido para requisição: {}", path);
                } else {
                    log.warn("Token JWT inválido ou expirado para requisição: {}", path);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\":\"Token JWT inválido ou expirado\"}");
                    return;
                }
            } catch (Exception e) {
                log.error("Erro ao validar token JWT", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Erro na validação do token JWT\"}");
                return;
            }
        } else {
            log.warn("Token JWT não encontrado na requisição: {}", path);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Token JWT obrigatório\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}

