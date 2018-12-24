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
    @Value("${im.clientId}")
    private String clientId;

    @Value("${im.clientSecret}")
    private String clientSecret;

    @Value("${im.url}")
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