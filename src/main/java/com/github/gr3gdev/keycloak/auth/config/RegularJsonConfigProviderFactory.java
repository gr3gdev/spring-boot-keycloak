package com.github.gr3gdev.keycloak.auth.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.keycloak.services.util.JsonConfigProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

public class RegularJsonConfigProviderFactory extends JsonConfigProviderFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegularJsonConfigProviderFactory.class);

    public RegularJsonConfigProviderFactory(DataSourceProperties dataSourceProperties) {
        final Path themesPath = Paths.get("/themes");
        if (Files.exists(themesPath)) {
            System.setProperty("keycloak.theme.dir", "/themes");
            try {
                LOGGER.info("Custom themes directories : {}",
                        Files.list(themesPath).map(Path::toString).collect(Collectors.joining(", ")));
            } catch (IOException e) {
                LOGGER.error("Unable to scan themes directories", e);
            }
        }
        System.setProperty("keycloak.connectionsJpa.url", dataSourceProperties.getUrl());
        System.setProperty("keycloak.connectionsJpa.driver", dataSourceProperties.getDriverClassName());
        System.setProperty("keycloak.connectionsJpa.user", dataSourceProperties.getUsername());
        System.setProperty("keycloak.connectionsJpa.password", dataSourceProperties.getPassword());
        LOGGER.info("Configure datasource {}", dataSourceProperties.getUrl());
    }
}
