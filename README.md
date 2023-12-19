# spring-boot-keycloak

Spring boot project with Keycloak embedded.

The docker's image is optimized with a custom JRE (~200MB).


## docker

Command line example :
```
docker run -p 8080:8080 \
  -e DATASOURCE_URL=jdbc:postgresql://localhost:5432/auth
  -e DATASOURCE_DRIVER=org.postgresql.Driver
  -e DATASOURCE_USER=user
  -e DATASOURCE_PASSWORD=password
  -e ADMIN_USERNAME=admin
  -e ADMIN_PASSWORD=admin
  -e REALM_FILE=/home/user/my_realm.json
  -e ROLES_FILE=/home/user/my_roles.json
  -e CLIENTS_FILE=/home/user/my_clients.json
  gr3gdev/spring-boot-keycloak
```

## docker-compose

```yaml
version: '3.3'

services:
  postgres:
    image: postgres:15.5-alpine
    ports:
      - 5432:5432
    environment:
      - POSTGRES_DB=auth
      - POSTGRES_USER=authuser
      - POSTGRES_PASSWORD=authpassword
    networks:
      - auth

  keycloak:
    image: gr3gdev/spring-boot-keycloak
    environment:
      - DATASOURCE_URL=jdbc:postgresql://dbauth:5432/auth
      - DATASOURCE_DRIVER=org.postgresql.Driver
      - DATASOURCE_USER=authuser
      - DATASOURCE_PASSWORD=authpassword
      - ADMIN_USERNAME=admin
      - ADMIN_PASSWORD=admin
      - REALM_FILE=/conf/realm.json
      - ROLES_FILE=/conf/roles.json
      - CLIENTS_FILE=/conf/clients.json
    volumes:
      - ./my_conf/keycloak:/conf
    depends_on:
      - dbauth
    networks:
      - auth

networks:
  auth:
```
