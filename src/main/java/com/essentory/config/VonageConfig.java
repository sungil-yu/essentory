package com.essentory.config;


import com.vonage.client.HttpConfig;
import com.vonage.client.VonageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VonageConfig {

    @Value("${vonage_auth.api_key}")
    private String API_KEY;

    @Value("${vonage_auth.api_secret}")
    private String API_SECRET_KEY;

    @Bean
    public VonageClient vonageClient() {
        return VonageClient.builder()
            .apiKey(API_KEY)
            .apiSecret(API_SECRET_KEY)
            .httpConfig(HttpConfig.defaultConfig())
            .build();
    }

}
