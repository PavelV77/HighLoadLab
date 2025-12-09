package com.example.authservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    
    @Bean
    @LoadBalanced
    public RestTemplate loadBalancedRestTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    @Primary
    public RestTemplate restTemplate() {
        // Обычный RestTemplate для прямых вызовов (без load balancer)
        return new RestTemplate();
    }
}

