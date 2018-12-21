package com.bosch.inst.base.security.im;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Import({SecureServletConfiguration.class})
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableSecurityServlet {

}