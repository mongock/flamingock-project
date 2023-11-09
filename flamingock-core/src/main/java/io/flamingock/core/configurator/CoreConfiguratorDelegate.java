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

package io.flamingock.core.configurator;


import io.flamingock.core.configurator.legacy.LegacyMigration;
import io.flamingock.core.pipeline.Stage;
import io.flamingock.template.TemplateFactory;
import io.flamingock.template.TemplateModule;

import java.util.Map;
import java.util.function.Supplier;

public class CoreConfiguratorDelegate<HOLDER> implements CoreConfigurator<HOLDER>{
    private final Supplier<HOLDER> holderSupplier;
    private final CoreConfiguration properties;

    public CoreConfiguratorDelegate(CoreConfiguration properties, Supplier<HOLDER> holderSupplier) {
        this.properties = properties;
        this.holderSupplier = holderSupplier;
    }

    @Override
    public CoreConfigurable getCoreProperties() {
        return properties;
    }

    @Override
    public HOLDER addStage(Stage stage) {
        properties.getStages().add(stage);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setLockAcquiredForMillis(long lockAcquiredForMillis) {
        properties.setLockAcquiredForMillis(lockAcquiredForMillis);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        properties.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        properties.setLockTryFrequencyMillis(lockTryFrequencyMillis);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        properties.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setTrackIgnored(boolean trackIgnored) {
        properties.setTrackIgnored(trackIgnored);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setEnabled(boolean enabled) {
        properties.setEnabled(enabled);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setStartSystemVersion(String startSystemVersion) {
        properties.setStartSystemVersion(startSystemVersion);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setEndSystemVersion(String endSystemVersion) {
        properties.setEndSystemVersion(endSystemVersion);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setServiceIdentifier(String serviceIdentifier) {
        properties.setServiceIdentifier(serviceIdentifier);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setMetadata(Map<String, Object> metadata) {
        properties.setMetadata(metadata);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setLegacyMigration(LegacyMigration legacyMigration) {
        properties.setLegacyMigration(legacyMigration);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setTransactionEnabled(Boolean transactionEnabled) {
        properties.setTransactionEnabled(transactionEnabled);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setDefaultAuthor(String publicMigrationAuthor) {
        properties.setDefaultAuthor(publicMigrationAuthor);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setTransactionStrategy(TransactionStrategy transactionStrategy) {
        properties.setTransactionStrategy(transactionStrategy);
        return holderSupplier.get();
    }

    @Override
    public HOLDER addTemplateModule(TemplateModule templateModule) {
        TemplateFactory.registerModule(templateModule);
        return holderSupplier.get();
    }


    @Override
    public long getLockAcquiredForMillis() {
        return properties.getLockAcquiredForMillis();
    }

    @Override
    public Long getLockQuitTryingAfterMillis() {
        return properties.getLockQuitTryingAfterMillis();
    }

    @Override
    public long getLockTryFrequencyMillis() {
        return properties.getLockTryFrequencyMillis();
    }

    @Override
    public boolean isThrowExceptionIfCannotObtainLock() {
        return properties.isThrowExceptionIfCannotObtainLock();
    }

    @Override
    public boolean isTrackIgnored() {
        return properties.isTrackIgnored();
    }

    @Override
    public boolean isEnabled() {
        return properties.isEnabled();
    }

    @Override
    public String getStartSystemVersion() {
        return properties.getStartSystemVersion();
    }

    @Override
    public String getEndSystemVersion() {
        return properties.getEndSystemVersion();
    }

    @Override
    public String getServiceIdentifier() {
        return properties.getServiceIdentifier();
    }

    @Override
    public Map<String, Object> getMetadata() {
        return properties.getMetadata();
    }

    @Override
    public LegacyMigration getLegacyMigration() {
        return properties.getLegacyMigration();
    }

    @Override
    public Boolean getTransactionEnabled() {
        return properties.getTransactionEnabled();
    }

    @Override
    public String getDefaultAuthor() {
        return properties.getDefaultAuthor();
    }

    @Override
    public TransactionStrategy getTransactionStrategy() {
        return properties.getTransactionStrategy();
    }

}
