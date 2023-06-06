package io.flamingock.oss.driver.mongodb.sync.v4.internal.mongodb;

import com.mongodb.annotations.NotThreadSafe;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import io.flamingock.core.core.util.Pair;
import io.flamingock.oss.driver.common.mongodb.MongoSync4SessionManagerGeneric;
import io.flamingock.oss.driver.common.mongodb.SessionManager;
import io.flamingock.oss.driver.common.mongodb.SessionWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@NotThreadSafe
public class MongoSync4SessionManager extends MongoSync4SessionManagerGeneric<ClientSession> {

    public MongoSync4SessionManager(MongoClient mongoClient) {
        super(mongoClient::startSession);
    }

}
