/*
 * Copyright 2023 Flamingock (https://oss.flamingock.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.springboot.v3.context;

import io.flamingock.core.configurator.CoreConfigurable;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.configurator.TransactionStrategy;
import io.flamingock.core.configurator.cloud.CloudConfigurable;
import io.flamingock.core.configurator.cloud.CloudConfiguration;
import io.flamingock.core.configurator.legacy.LegacyMigration;
import io.flamingock.core.configurator.local.LocalConfigurable;
import io.flamingock.core.configurator.local.LocalConfiguration;
import io.flamingock.core.pipeline.Stage;
import io.flamingock.springboot.v3.configurator.SpringRunnerType;
import io.flamingock.springboot.v3.configurator.SpringbootConfigurable;
import io.flamingock.springboot.v3.configurator.SpringbootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties("flamingock")
public class FlamingockConfigurationProperties
        implements
        CoreConfigurable,
        LocalConfigurable,
        CloudConfigurable,
        SpringbootConfigurable {
    private SpringRunnerType runnerType = SpringRunnerType.ApplicationRunner;

    private final CoreConfiguration coreConfiguration = new CoreConfiguration();

    private final SpringbootConfiguration springbootConfiguration = new SpringbootConfiguration();

    private final CloudConfiguration cloudConfiguration = new CloudConfiguration();

    private final LocalConfiguration localConfiguration = new LocalConfiguration();

    public CoreConfiguration getCoreProperties() {
        return coreConfiguration;
    }

    public SpringbootConfiguration getSpringbootProperties() {
        return springbootConfiguration;
    }

    public LocalConfiguration getLocalProperties() {
        return localConfiguration;
    }

    public CloudConfiguration getCloudProperties() {
        return cloudConfiguration;
    }


    @Override
    public void setStages(List<Stage> stages) {
        coreConfiguration.setStages(stages);
    }

    @Override
    public List<Stage> getStages() {
        return coreConfiguration.getStages();
    }

    @Override
    public void setLockAcquiredForMillis(long lockAcquiredForMillis) {
        coreConfiguration.setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    @Override
    public void setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        coreConfiguration.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    @Override
    public void setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        coreConfiguration.setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    @Override
    public void setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        coreConfiguration.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    @Override
    public void setTrackIgnored(boolean trackIgnored) {
        coreConfiguration.setTrackIgnored(trackIgnored);
    }

    @Override
    public void setEnabled(boolean enabled) {
        coreConfiguration.setEnabled(enabled);
    }

    @Override
    public void setStartSystemVersion(String startSystemVersion) {
        coreConfiguration.setStartSystemVersion(startSystemVersion);
    }

    @Override
    public void setEndSystemVersion(String endSystemVersion) {
        coreConfiguration.setEndSystemVersion(endSystemVersion);
    }

    @Override
    public void setServiceIdentifier(String serviceIdentifier) {
        coreConfiguration.setServiceIdentifier(serviceIdentifier);
    }

    @Override
    public void setMetadata(Map<String, Object> metadata) {
        coreConfiguration.setMetadata(metadata);
    }

    @Override
    public void setLegacyMigration(LegacyMigration legacyMigration) {
        coreConfiguration.setLegacyMigration(legacyMigration);
    }

    @Override
    public void setTransactionEnabled(Boolean transactionEnabled) {
        coreConfiguration.setTransactionEnabled(transactionEnabled);
    }

    @Override
    public void setDefaultAuthor(String defaultAuthor) {
        coreConfiguration.setDefaultAuthor(defaultAuthor);
    }

    @Override
    public void setTransactionStrategy(TransactionStrategy transactionStrategy) {
        coreConfiguration.setTransactionStrategy(transactionStrategy);
    }

    @Override
    public long getLockAcquiredForMillis() {
        return coreConfiguration.getLockAcquiredForMillis();
    }

    @Override
    public Long getLockQuitTryingAfterMillis() {
        return coreConfiguration.getLockQuitTryingAfterMillis();
    }

    @Override
    public long getLockTryFrequencyMillis() {
        return coreConfiguration.getLockTryFrequencyMillis();
    }

    @Override
    public boolean isThrowExceptionIfCannotObtainLock() {
        return coreConfiguration.isThrowExceptionIfCannotObtainLock();
    }

    @Override
    public boolean isTrackIgnored() {
        return coreConfiguration.isTrackIgnored();
    }

    @Override
    public boolean isEnabled() {
        return coreConfiguration.isEnabled();
    }

    @Override
    public String getStartSystemVersion() {
        return coreConfiguration.getStartSystemVersion();
    }

    @Override
    public String getEndSystemVersion() {
        return coreConfiguration.getEndSystemVersion();
    }

    @Override
    public String getServiceIdentifier() {
        return coreConfiguration.getServiceIdentifier();
    }

    @Override
    public Map<String, Object> getMetadata() {
        return coreConfiguration.getMetadata();
    }

    @Override
    public LegacyMigration getLegacyMigration() {
        return coreConfiguration.getLegacyMigration();
    }

    @Override
    public Boolean getTransactionEnabled() {
        return coreConfiguration.getTransactionEnabled();
    }

    @Override
    public String getDefaultAuthor() {
        return coreConfiguration.getDefaultAuthor();
    }

    @Override
    public TransactionStrategy getTransactionStrategy() {
        return coreConfiguration.getTransactionStrategy();
    }

    @Override
    public SpringRunnerType getRunnerType() {
        return runnerType;
    }

    @Override
    public void setRunnerType(SpringRunnerType runnerType) {
        this.runnerType = runnerType;
    }


    @Override
    public void setApiKey(String apiKey) {
        cloudConfiguration.setApiKey(apiKey);
    }

    @Override
    public void setToken(String token) {
        cloudConfiguration.setToken(token);
    }

    @Override
    public String getApiKey() {
        return cloudConfiguration.getApiKey();
    }

    @Override
    public String getToken() {
        return cloudConfiguration.getToken();
    }

    public boolean isCloudConfigurationEmpty() {
        return cloudConfiguration.getApiKey() == null
        && cloudConfiguration.getToken() == null;
    }
}