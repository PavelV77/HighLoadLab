package com.example.Gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class StaticResourceFilter implements WebFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(StaticResourceFilter.class);

    private static final List<String> API_PATHS = Arrays.asList(
        "/auth", "/api", "/users", "/news", "/notifications", "/actuator"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        
        // Если это API путь, пропускаем дальше для обработки Gateway маршрутами
        if (isApiPath(path)) {
            logger.debug("API path detected, passing to Gateway: {}", path);
            return chain.filter(exchange);
        }

        logger.debug("Non-API path detected, trying to serve static resource: {}", path);

        // Для не-API путей пытаемся найти статический ресурс
        Resource resource = getResource(path);
        
        if (resource.exists() && resource.isReadable()) {
            logger.debug("Serving static resource: {}", path);
            return serveStaticResource(exchange, resource);
        }

        // Если файл не найден, отдаем index.html (SPA fallback для Angular routing)
        Resource indexResource = new ClassPathResource("static/index.html");
        if (indexResource.exists() && indexResource.isReadable()) {
            logger.debug("Resource not found, serving index.html for SPA routing: {}", path);
            return serveStaticResource(exchange, indexResource);
        }

        // Если index.html тоже не найден, логируем предупреждение и пропускаем дальше
        logger.warn("Static resources not found! index.html missing. Make sure frontend is built and copied to resources/static");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // Высокий приоритет, выполняется раньше Gateway
        // API пути пропускаются дальше для Gateway обработки
        return -1;
    }

    private boolean isApiPath(String path) {
        return API_PATHS.stream().anyMatch(path::startsWith);
    }

    private Resource getResource(String path) {
        if (path.equals("/") || path.isEmpty()) {
            return new ClassPathResource("static/index.html");
        }
        // Убираем ведущий слеш для поиска ресурса
        String resourcePath = path.startsWith("/") ? path.substring(1) : path;
        return new ClassPathResource("static/" + resourcePath);
    }

    private Mono<Void> serveStaticResource(ServerWebExchange exchange, Resource resource) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        
        // Определяем Content-Type по расширению файла
        String filename = resource.getFilename();
        if (filename != null) {
            if (filename.endsWith(".html")) {
                response.getHeaders().setContentType(MediaType.TEXT_HTML);
            } else if (filename.endsWith(".js")) {
                response.getHeaders().setContentType(MediaType.parseMediaType("application/javascript"));
            } else if (filename.endsWith(".css")) {
                response.getHeaders().setContentType(MediaType.parseMediaType("text/css"));
            } else if (filename.endsWith(".ico")) {
                response.getHeaders().setContentType(MediaType.parseMediaType("image/x-icon"));
            } else if (filename.endsWith(".png")) {
                response.getHeaders().setContentType(MediaType.IMAGE_PNG);
            } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
                response.getHeaders().setContentType(MediaType.IMAGE_JPEG);
            } else if (filename.endsWith(".json")) {
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            }
        }
        
        try {
            byte[] bytes = resource.getInputStream().readAllBytes();
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer))
                .doOnError(error -> DataBufferUtils.release(buffer));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}

