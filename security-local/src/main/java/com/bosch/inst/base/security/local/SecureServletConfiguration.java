package com.bosch.inst.base.security.local;

import com.bosch.inst.base.security.local.servlet.SecurityConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Profile("security-local")
@Import({SecurityConfiguration.class})
@Configuration
public class SecureServletConfiguration {

}
