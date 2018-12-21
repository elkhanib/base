package com.bosch.inst.base.security.local.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConditionalOnProperty(name = "im.enabled", havingValue = "false")
@ConfigurationProperties("auth")
@Getter
public class AuthenticationProperties {

    /**
     * Cookie config
     */
    private Cookie cookie = new Cookie();

    @Getter
    @Setter
    public static class Cookie {

        /**
         * Name of the cookie.
         */
        private String name = "TOKEN";

        /**
         * Domain for the session cookie.
         */
        private String domain;

        /**
         * Path of the session cookie.
         */
        private String path = "/";

        /**
         * "HttpOnly" flag for the session cookie.
         */
        private Boolean httpOnly;

        /**
         * "Secure" flag for the session cookie.
         */
        private Boolean secure = true;

        /**
         * Maximum age of the session cookie in seconds.
         */
        private Integer maxAge = -1;

    }
}

