package com.github.gr3gdev.keycloak.auth.config;

import java.util.NoSuchElementException;

import org.keycloak.Config;
import org.keycloak.exportimport.ExportImportManager;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resources.KeycloakApplication;
import org.keycloak.services.util.JsonConfigProviderFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

public class EmbeddedKeycloakApplication extends KeycloakApplication {

    public static KeycloakServerProperties keycloakServerProperties;
    public static DataSourceProperties dataSourceProperties;

    @Override
    protected void loadConfig() {
        JsonConfigProviderFactory factory = new RegularJsonConfigProviderFactory(dataSourceProperties);
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
