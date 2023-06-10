package io.flamingock.oss.driver.common.mongodb;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class SessionManager<CLIENT_SESSION> {

    private final Map<String, CLIENT_SESSION> sessionMap;
    private final Supplier<CLIENT_SESSION> clientSessionSupplier;

    public SessionManager(Supplier<CLIENT_SESSION> clientSessionSupplier) {
        this.clientSessionSupplier = clientSessionSupplier;
        sessionMap = new HashMap<>();
    }

    /**
     * Starts a session, if not already created or is closed. Otherwise, it returns the existing one.
     *
     * @param sessionId ClientSession identifier. Will be the taskId most of the time(if not always)
     * @return ClientSession
     */
    public CLIENT_SESSION startSession(String sessionId) {
        return sessionMap.compute(sessionId, (k, currentSession) ->
                currentSession == null ? clientSessionSupplier.get() : currentSession
        );
    }

    public void closeSession(String sessionId) {
        sessionMap.remove(sessionId);
    }

    public Optional<CLIENT_SESSION> getSession(String sessionId) {
        return Optional.ofNullable(sessionMap.get(sessionId));
    }
}