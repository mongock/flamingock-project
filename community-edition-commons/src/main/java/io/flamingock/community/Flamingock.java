package io.flamingock.community;

import io.flamingock.core.builder.CommunityFlamingockBuilder;
import io.flamingock.core.builder.FlamingockFactory;

public class Flamingock {
    public static CommunityFlamingockBuilder builder() {
        return FlamingockFactory.getCommunityBuilder();
    }
}
