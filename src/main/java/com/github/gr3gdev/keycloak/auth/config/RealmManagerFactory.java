package com.github.gr3gdev.keycloak.auth.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RolesRepresentation;
import org.keycloak.services.managers.ApplianceBootstrap;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.util.JsonSerialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.gr3gdev.keycloak.auth.config.KeycloakServerProperties.AdminUser;

public class RealmManagerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(RealmManagerFactory.class);

    public void createMasterRealmAdminUser(KeycloakSessionFactory sessionFactory,
            KeycloakServerProperties keycloakServerProperties) {
        KeycloakSession session = sessionFactory.create();
        ApplianceBootstrap applianceBootstrap = new ApplianceBootstrap(session);
        AdminUser admin = keycloakServerProperties.getAdminUser();
        try {
            session.getTransactionManager().begin();
            applianceBootstrap.createMasterRealmUser(admin.getUsername(), admin.getPassword());
            session.getTransactionManager().commit();
        } catch (Exception ex) {
            LOG.warn("Couldn't create keycloak master admin user: {}", ex.getMessage());
            session.getTransactionManager().rollback();
        }
        session.close();
    }

    private InputStream loadFile(String param) throws IOException {
        return Files.newInputStream(ResourceUtils.getFile("file:" + param).toPath(), StandardOpenOption.READ);
    }

    public void createRealm(KeycloakSessionFactory sessionFactory, KeycloakServerProperties keycloakServerProperties) {
        KeycloakSession session = sessionFactory.create();
        try {
            session.getTransactionManager().begin();

            final RealmManager manager = new RealmManager(session);

            if (StringUtils.isNotBlank(keycloakServerProperties.getRealmFile())) {
                final RealmRepresentation realmRepresentation = JsonSerialization.readValue(
                        loadFile(keycloakServerProperties.getRealmFile()),
                        RealmRepresentation.class);

                if (StringUtils.isNotBlank(keycloakServerProperties.getRolesFile())) {
                    final RolesRepresentation roles = JsonSerialization.readValue(
                            loadFile(keycloakServerProperties.getRolesFile()),
                            RolesRepresentation.class);
                    realmRepresentation.setRoles(roles);
                }

                if (StringUtils.isNotBlank(keycloakServerProperties.getClientsFile())) {
                    final List<ClientRepresentation> clients = JsonSerialization.readValue(
                            loadFile(keycloakServerProperties.getClientsFile()),
                            new TypeReference<List<ClientRepresentation>>() {
                            });
                    realmRepresentation.setClients(clients);
                }

                final RealmModel realm = manager.getRealmByName(realmRepresentation.getRealm());
                if (realm == null) {
                    LOG.info("Create Realm : {}", realmRepresentation.getRealm());
                    manager.importRealm(realmRepresentation);
                } else {
                    LOG.info("Update Realm");
                    // TODO
                }

            }

            session.getTransactionManager().commit();
        } catch (Exception ex) {
            LOG.warn("Failed to create Realm", ex);
            session.getTransactionManager().rollback();
        }
        session.close();
    }

}
