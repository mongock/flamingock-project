package io.flamingock.examples.community.couchbase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.flamingock.examples.community.couchbase.changes.IndexInitializerChangeUnit;
import io.flamingock.examples.community.couchbase.config.CouchbaseInitializer;
import io.flamingock.examples.community.couchbase.events.FailureEventListener;
import io.flamingock.examples.community.couchbase.events.StartedEventListener;
import io.flamingock.examples.community.couchbase.events.SuccessEventListener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Import({CommunitySpringbootV2CouchbaseApp.class})
@ContextConfiguration(initializers = CouchbaseInitializer.class)
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
        assertEquals(2, ChangesTracker.changes.size());
        assertEquals(IndexInitializerChangeUnit.class.getName() + "_beforeExecution", ChangesTracker.changes.get(0));
        assertEquals(IndexInitializerChangeUnit.class.getName() + "_execution", ChangesTracker.changes.get(1));
    }

    @Test
    @DisplayName("SHOULD trigger start and success event WHEN executed IF happy path")
    void events() {
        assertTrue(startedEventListener.executed);
        assertTrue(successEventListener.executed);
        assertFalse(failureEventListener.executed);
    }
}