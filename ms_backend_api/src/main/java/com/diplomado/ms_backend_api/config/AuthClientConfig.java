package com.diplomado.ms_backend_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AuthClientConfig {
    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }
}
