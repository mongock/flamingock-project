package io.flamingock.examples.community;

import io.flamingock.examples.community.changes.ACreateCollection;
import io.flamingock.examples.community.changes.BInsertDocument;
import io.flamingock.examples.community.changes.CInsertAnotherDocument;
import io.flamingock.examples.community.config.MongoInitializer;
import io.flamingock.examples.community.events.FailureEventListener;
import io.flamingock.examples.community.events.StartedEventListener;
import io.flamingock.examples.community.events.SuccessEventListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Import({CommunitySpringbootMongodbSpringdataApp.class})
@ContextConfiguration(initializers = MongoInitializer.class)
class SuccessExecutionTest {

    @Autowired
    private StartedEventListener startedEventListener;

    @Autowired
    private SuccessEventListener successEventListener;

    @Autowired
    private FailureEventListener failureEventListener;


    @Test
    @DisplayName("SHOULD execute all the expected changes WHEN executed IF happy path")
    void allExpectedChangeExecuted() {
        assertEquals(3, ChangesTracker.size());
        assertEquals(ACreateCollection.class.getName(), ChangesTracker.get(0));
        assertEquals(BInsertDocument.class.getName(), ChangesTracker.get(1));
        assertEquals(CInsertAnotherDocument.class.getName(), ChangesTracker.get(2));
    }

    @Test
    @DisplayName("SHOULD trigger start and success event WHEN executed IF happy path")
    void events() {
        assertTrue(startedEventListener.executed);
        assertTrue(successEventListener.executed);
        assertFalse(failureEventListener.executed);
    }
}