package io.flamingock.examples.community.mongodb.sync;

import io.flamingock.examples.community.mongodb.sync.changes.ACreateCollection;
import io.flamingock.examples.community.mongodb.sync.changes.BInsertDocument;
import io.flamingock.examples.community.mongodb.sync.changes.CInsertAnotherDocument;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.flamingock.examples.community.mongodb.sync.MongoDBTestHelper.mongoClient;
import static io.flamingock.examples.community.mongodb.sync.MongoDBTestHelper.mongoDatabase;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommunityStandaloneMongodbSyncAppTest {

    public static final String DB_NAME = "test";
    public static final String AUDIT_LOG_COLLECTION = "mongockChangeLog";
    public static final String CLIENTS_COLLECTION = "clientCollection";


    @BeforeEach
    public void setupEach() {
        mongoDatabase.getCollection(AUDIT_LOG_COLLECTION).deleteMany(new Document());
    }


    @Test
    void happyPath() {
        //Given-When
        new CommunityStandaloneMongodbSyncApp().run(mongoClient, DB_NAME);
        assertEquals(ACreateCollection.class.getName(), ChangesTracker.changes.get(0));
        assertEquals(BInsertDocument.class.getName(), ChangesTracker.changes.get(1));
        assertEquals(CInsertAnotherDocument.class.getName(), ChangesTracker.changes.get(2));

        //tear-down
        mongoDatabase.getCollection(CLIENTS_COLLECTION).drop();

    }

}
