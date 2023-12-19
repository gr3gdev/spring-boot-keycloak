package com.github.gr3gdev.doglife.auth.config;

import org.keycloak.services.util.JsonConfigProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

public class RegularJsonConfigProviderFactory extends JsonConfigProviderFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegularJsonConfigProviderFactory.class);

    public RegularJsonConfigProviderFactory(DataSourceProperties dataSourceProperties) {
        System.setProperty("keycloak.connectionsJpa.url", dataSourceProperties.getUrl());
        System.setProperty("keycloak.connectionsJpa.driver", dataSourceProperties.getDriverClassName());
        System.setProperty("keycloak.connectionsJpa.user", dataSourceProperties.getUsername());
        System.setProperty("keycloak.connectionsJpa.password", dataSourceProperties.getPassword());
        LOGGER.info("Configure datasource {}", dataSourceProperties.getUrl());
    }
}
