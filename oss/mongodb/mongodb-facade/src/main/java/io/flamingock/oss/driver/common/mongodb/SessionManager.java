package io.flamingock.oss.driver.common.mongodb;

import java.util.Optional;


public interface SessionManager<CLIENT_SESSION> {



    /**
     * Starts a session, if not already created or is closed. Otherwise, it returns the existing one.
     *
     * @param sessionId ClientSession identifier. Will be the taskId most of the time(if not always)
     * @return ClientSession
     */
    CLIENT_SESSION  startSession(String sessionId);

    void  closeSession(String sessionId);

    Optional<CLIENT_SESSION> getSession(String sessionId);
}
