package com.example.authservice.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.example.authservice.dto.KursNewsUserDto;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceClient.class);
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired(required = false)
    private DiscoveryClient discoveryClient;
    
    private String getKursNewsServiceUrl() {
        // Используем Gateway для единообразия
        // Можно также использовать lb://kursnews-service, если добавить spring-cloud-starter-loadbalancer
        return "http://localhost:8080";
    }
    
    public void createUserInKursNews(UUID userId, String login) {
        try {
            String serviceUrl = getKursNewsServiceUrl();
            String url = serviceUrl + "/users";
            
            logger.info("Creating user in KursNews service: userId={}, login={}, url={}", userId, login, url);
            
            // Создаем UserDto для KursNews
            long currentTime = System.currentTimeMillis();
            KursNewsUserDto userDto = new KursNewsUserDto(
                userId,
                login,
                currentTime,
                currentTime
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<KursNewsUserDto> request = new HttpEntity<>(userDto, headers);
            
            logger.info("Sending request to KursNews: url={}, userId={}, login={}", url, userId, login);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
            );
            
            logger.info("Response from KursNews: status={}, body={}", response.getStatusCode(), response.getBody());
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Successfully created user in KursNews service: userId={}, login={}", userId, login);
            } else {
                logger.error("Failed to create user in KursNews service. Status: {}, Response: {}", 
                    response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to create user in KursNews: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (RestClientException e) {
            logger.error("Error creating user in KursNews service: {}", e.getMessage(), e);
            // Не пробрасываем исключение, чтобы не прерывать процесс регистрации
        } catch (Exception e) {
            logger.error("Unexpected error creating user in KursNews service: {}", e.getMessage(), e);
        }
    }
}

