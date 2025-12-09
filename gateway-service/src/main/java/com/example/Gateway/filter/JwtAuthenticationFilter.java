package com.example.Gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    @Value("${jwt.secret:mySecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLongForHS256Algorithm}")
    private String jwtSecret;
    
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
        "/auth/login",
        "/auth/register",
        "/actuator"
    );
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        if (isPublicEndpoint(path)) {
            logger.debug("Public endpoint, skipping authentication: {}", path);
            return chain.filter(exchange);
        }
        
        String authHeader = request.getHeaders().getFirst("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Missing or invalid Authorization header for path: {}", path);
            return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }
        
        String token = authHeader.substring(7);
        
        try {
            if (validateToken(token)) {
                logger.debug("Token validated successfully for path: {}", path);
                return chain.filter(exchange);
            } else {
                logger.warn("Invalid token for path: {}", path);
                return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            logger.error("Error validating token: {}", e.getMessage());
            return onError(exchange, "Token validation failed", HttpStatus.UNAUTHORIZED);
        }
    }
    
    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }
    
    private boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            return claims.getExpiration().after(new java.util.Date());
        } catch (Exception e) {
            logger.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }
    
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");
        
        String body = "{\"error\":\"" + message + "\",\"status\":" + status.value() + "}";
        return response.writeWith(
            Mono.just(response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8)))
        );
    }
    
    @Override
    public int getOrder() {
        return -67;
    }
}

