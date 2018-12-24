package com.bosch.inst.base.security.im;

import com.bosch.inst.base.security.im.login.LoginController;
import com.bosch.inst.base.security.im.servlet.NonCloudIClientConfiguration;
import com.bosch.inst.base.security.im.servlet.SecurityConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Profile("security-im")
@Import({SecurityConfiguration.class, LoginController.class, NonCloudIClientConfiguration.class})
@Configuration
public class SecureServletConfiguration {

}
