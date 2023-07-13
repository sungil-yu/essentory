package com.essentory.config

import com.vonage.client.HttpConfig
import com.vonage.client.VonageClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class VonageConfig {
    @Value("\${vonage_auth.api_key}")
    val apiKey: String? = null

    @Value("\${vonage_auth.api_secret}")
    val apiSecretKey: String? = null

    @Bean
    fun vonageClient(): VonageClient {
        return VonageClient.builder()
                .apiKey(apiKey)
                .apiSecret(apiSecretKey)
                .httpConfig(HttpConfig.defaultConfig())
                .build()
    }
}
