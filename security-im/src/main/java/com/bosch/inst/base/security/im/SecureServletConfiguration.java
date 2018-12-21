package com.bosch.inst.base.security.im;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = SecureServletConfiguration.class)
public class SecureServletConfiguration {

}
