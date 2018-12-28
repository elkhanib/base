package com.bosch.inst.base.security.im.servlet;

import com.bosch.im.api2.client.IClient;
import com.bosch.im.api2.client.Permissions;
import com.bosch.im.spring.config.ImStarterConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
@Profile("!cloud")
@Import(ImStarterConfiguration.class)
public class NonCloudIClientConfiguration {
    @Value("${im.clientId:d0953a11-080e-4c24-a773-d449cb834a28}")
    private String clientId;

    @Value("${im.clientSecret:DcQ1BwkqN4ii5F4jwS37hy}")
    private String clientSecret;

    @Value("${im.url:https://permissions-api.apps.bosch-iot-cloud.com}")
    private String serviceUrl;

    @Bean
    @Primary
    public IClient nonCloudClient() {
        return Permissions.createClientBuilder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .serviceUrl(serviceUrl)
                .build();
    }
}