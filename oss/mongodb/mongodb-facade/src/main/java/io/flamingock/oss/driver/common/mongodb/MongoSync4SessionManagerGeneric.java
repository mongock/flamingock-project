package io.flamingock.oss.driver.common.mongodb;

import io.flamingock.core.core.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class MongoSync4SessionManagerGeneric<CLIENT_SESSION> implements SessionManager<CLIENT_SESSION> {

    //Pair<ClientSession, Boolean>-> Boolean tells if it's closed
    private final Map<String, Pair<CLIENT_SESSION, Boolean>> sessionMap;
    private final Supplier<CLIENT_SESSION> clientSessionSupplier;

    public MongoSync4SessionManagerGeneric(Supplier<CLIENT_SESSION> clientSessionSupplier) {
        this.clientSessionSupplier = clientSessionSupplier;
        sessionMap = new HashMap<>();
    }

    /**
     * Starts a session, if not already created or is closed. Otherwise, it returns the existing one.
     *
     * @param sessionId ClientSession identifier. Will be the taskId most of the time(if not always)
     * @return ClientSession
     */
    @Override
    public CLIENT_SESSION startSession(String sessionId) {
        return sessionMap.compute(sessionId, (k, currentSession) ->
                currentSession == null || !currentSession.getSecond()
                        ? new Pair<>(clientSessionSupplier.get(), true)
                        : currentSession
        ).getFirst();
    }

    @Override
    public Optional<CLIENT_SESSION> getSession(String sessionId) {
        return Optional.ofNullable(sessionMap.get(sessionId).getFirst());
    }
}
