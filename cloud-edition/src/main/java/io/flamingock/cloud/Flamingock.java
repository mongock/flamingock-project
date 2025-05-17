package io.flamingock.cloud;

import io.flamingock.core.builder.CloudFlamingockBuilder;
import io.flamingock.core.builder.FlamingockFactory;

public class Flamingock {
    public static CloudFlamingockBuilder builder() {
        return FlamingockFactory.getCloudBuilder();
    }
}
