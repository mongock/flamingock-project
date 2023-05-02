package io.mongock.core.runner;

import io.mongock.core.configuration.AbstractConfiguration;
import io.mongock.core.configuration.LegacyMigration;
import io.mongock.core.configuration.TransactionStrategy;

import java.util.List;
import java.util.Map;

public interface RunnerBuilder {

    Runner build();
}
