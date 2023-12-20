package com.github.gr3gdev.keycloak.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "keycloak.server")
@Data
public class KeycloakServerProperties {

    private String contextPath = "/auth";

    private String realmFile;
    private String rolesFile;
    private String clientsFile;

    private AdminUser adminUser = new AdminUser();

    @Data
    public static class AdminUser {
        private String username = "admin";
        private String password = "admin";
    }
}
