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
    @Value("${jwt.header:X-Access-Token}")
    private String header;

    @Bean
    @Primary
    public IClient nonCloudClient() {
        return Permissions.createClientBuilder()
                .clientId("e00402af-7658-4827-86dc-60d25b4f06be")
                .clientSecret("HSDLSuN3jmOPWQ9viqI8Iq")
                .serviceUrl("https://permissions-api.s-apps.de1.bosch-iot-cloud.com/")
                .build();
    }
}