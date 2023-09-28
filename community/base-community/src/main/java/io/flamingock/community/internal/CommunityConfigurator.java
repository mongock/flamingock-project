package io.flamingock.community.internal;

import io.flamingock.community.internal.driver.ConnectionDriver;

import java.util.Collections;
import java.util.List;

public interface CommunityConfigurator<HOLDER> {
    HOLDER setDriver(ConnectionDriver<?> connectionDriver);

    ConnectionDriver<?> getDriver();
    
    boolean isIndexCreation();

    HOLDER setIndexCreation(boolean value);

    CommunityConfiguration getCommunityProperties();
}
