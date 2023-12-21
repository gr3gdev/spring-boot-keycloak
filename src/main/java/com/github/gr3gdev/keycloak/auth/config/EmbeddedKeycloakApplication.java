package com.github.gr3gdev.keycloak.auth.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.keycloak.Config;
import org.keycloak.exportimport.ExportImportManager;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resources.KeycloakApplication;
import org.keycloak.services.util.JsonConfigProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

public class EmbeddedKeycloakApplication extends KeycloakApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedKeycloakApplication.class);

    public static KeycloakServerProperties keycloakServerProperties;
    public static DataSourceProperties dataSourceProperties;

    @Override
    protected void loadConfig() {
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

        final JsonConfigProviderFactory factory = new RegularJsonConfigProviderFactory();
        Config.init(factory.create().orElseThrow(() -> new NoSuchElementException("No value present")));
    }

    @Override
    protected ExportImportManager bootstrap() {
        final ExportImportManager exportImportManager = super.bootstrap();
        final RealmManagerFactory realmManagerFactory = new RealmManagerFactory();
        final KeycloakSessionFactory sessionFactory = getSessionFactory();
        realmManagerFactory.createMasterRealmAdminUser(sessionFactory, keycloakServerProperties);
        realmManagerFactory.createRealm(sessionFactory, keycloakServerProperties);
        return exportImportManager;
    }

}
