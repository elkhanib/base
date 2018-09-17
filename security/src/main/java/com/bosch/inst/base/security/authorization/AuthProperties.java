package com.bosch.inst.base.security.authorization;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("auth")
public class AuthProperties {

    /**
     * Cookie configuration
     */
    private Cookie cookie = new Cookie();

    public Cookie getCookie() {
        return cookie;
    }

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
        private Integer maxAge;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Boolean getHttpOnly() {
            return httpOnly;
        }

        public void setHttpOnly(Boolean httpOnly) {
            this.httpOnly = httpOnly;
        }

        public Boolean getSecure() {
            return secure;
        }

        public void setSecure(Boolean secure) {
            this.secure = secure;
        }

        public Integer getMaxAge() {
            return maxAge;
        }

        public void setMaxAge(Integer maxAge) {
            this.maxAge = maxAge;
        }
    }
}

