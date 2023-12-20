package com.github.gr3gdev.keycloak.auth.config;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.common.util.ResteasyProvider;

public class Resteasy3Provider implements ResteasyProvider {

    @Override
    public <R> R getContextData(Class<R> type) {
        return ResteasyProviderFactory.getInstance()
                .getContextData(type);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void pushDefaultContextObject(Class type, Object instance) {
        ResteasyProviderFactory.getInstance()
                .getContextData(Dispatcher.class)
                .getDefaultContextObjects()
                .put(type, instance);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void pushContext(Class type, Object instance) {
        ResteasyContext.pushContext(type, instance);
    }

    @Override
    public void clearContextData() {
        ResteasyContext.clearContextData();
    }

}
