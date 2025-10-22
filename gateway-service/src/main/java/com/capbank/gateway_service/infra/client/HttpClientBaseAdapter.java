package com.capbank.gateway_service.infra.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpClientBaseAdapter {

    private final RestTemplate restTemplate;

    public HttpClientBaseAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> T post(String url, Object body, Class<T> responseType) {
        ResponseEntity<T> response = restTemplate.postForEntity(url, body, responseType);
        return response.getBody();
    }
}
