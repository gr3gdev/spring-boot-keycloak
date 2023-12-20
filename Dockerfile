# Build
FROM gradle:jdk21-alpine as build
RUN mkdir /spring-boot-keycloak
COPY . /spring-boot-keycloak
WORKDIR /spring-boot-keycloak
RUN gradle clean build

# Optimize JRE
FROM gradle:jdk21-alpine as customjre
RUN mkdir /work
COPY --from=build /spring-boot-keycloak/build/libs/spring-boot-keycloak-0.0.1-SNAPSHOT.jar /work/spring-boot-keycloak.jar
WORKDIR /work
RUN jar xf spring-boot-keycloak.jar
# find modules dependencies
RUN jdeps --ignore-missing-deps -q  \
    --recursive  \
    --multi-release 21  \
    --print-module-deps  \
    --class-path 'BOOT-INF/lib/*' \
    spring-boot-keycloak.jar > deps.info
# create a custom jre
RUN jlink \
    --add-modules $(cat deps.info) \
    --strip-debug \
    --compress 2 \
    --no-header-files \
    --no-man-pages \
    --output /customjre

# Slim image
FROM alpine as spring-boot-keycloak
ENV JAVA_HOME /jre
ENV PATH $JAVA_HOME/bin:$PATH
COPY --from=customjre /customjre $JAVA_HOME
RUN mkdir /libs /themes
COPY --from=customjre /work/spring-boot-keycloak.jar /libs/spring-boot-keycloak.jar
WORKDIR /libs
ENTRYPOINT java -jar spring-boot-keycloak.jar
