package io.flamingock.oss.driver.mongodb.sync.v4.internal.mongodb;

import com.mongodb.annotations.NotThreadSafe;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import io.flamingock.core.core.util.Pair;
import io.flamingock.oss.driver.common.mongodb.SessionManager;
import io.flamingock.oss.driver.common.mongodb.SessionWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@NotThreadSafe
public class MongoSync4SessionManager implements SessionManager<ClientSession> {

    private final MongoClient mongoClient;

    //Pair<ClientSession, Boolean>-> Boolean tells if it's closed
    private final Map<String, Pair<ClientSession, Boolean>> sessionMap;

    public MongoSync4SessionManager(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
        sessionMap = new HashMap<>();
    }

    /**
     * Starts a session, if not already created or is closed. Otherwise, it returns the existing one.
     *
     * @param sessionId ClientSession identifier. Will be the taskId most of the time(if not always)
     * @return ClientSession
     */
    @Override
    public ClientSession startSession(String sessionId) {
        return sessionMap.compute(sessionId, (k, currentSession) ->
                currentSession == null || !currentSession.getSecond()
                        ? new Pair<>(mongoClient.startSession(), true)
                        : currentSession
        ).getFirst();
    }

    @Override
    public Optional<ClientSession> getSession(String sessionId) {
        return Optional.ofNullable(sessionMap.get(sessionId).getFirst());
    }
}
